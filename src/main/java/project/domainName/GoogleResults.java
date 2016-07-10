package project.domainName;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class GoogleResults {
	public Elements links;
	public LinkedHashMap<String, Integer> urlHashMap;
	//Returns top 10 matches from google search.
	public String[] googleDomainName( String companyName,int numberOfUrls) throws UnsupportedEncodingException, IOException
	{
		
        App obj = new App();
        
        setLinks(companyName,numberOfUrls);
        
    	Elements links = getLinks();
    	//this.links = generateURLs(companyName,numberOfUrls);	//Generates google search urls.
		
    	LinkedHashMap<String, Integer> urlHashMap = new LinkedHashMap<String, Integer>();
    	String[] result = new String[20];   	
    	int linkCount = 1, delta=0;
    	
    	companyName = modifyCompanyName(companyName);			//Removes pvt. inc. etc. from the company name.
	    
    	for (Element link : links) 								//Iterates through the links.
    	{
    	    String title = link.text();
    	    String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
    	    url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

    	    if (!url.startsWith("http")||skipUrlFromUrl(url)||skipUrlFromTitle(title)) {
    	        //System.out.println("Skipped URL :" + url + " | Title :"+title);
    	    	continue; // Ads/news/apps/articles etc.
    	    }
    	    
    	    String homeUrl = obj.getHomePageURL(url);		//Get home page url based on google search result url.
    	    
    	    if(url.charAt(url.length() - 1)!='/')
    	    	url = url + "/";
    	    
    	    System.out.println("URL: " +url);
    	    
    	    title = title.replaceAll("\\s+","");						//Removes white spaces from the title.
    	    title = title.toLowerCase();
    	    companyName = companyName.replaceAll("\\s+","");			//Removes white spaces from the company name.
    	    int count = url.length() - url.replace("/", "").length();	//Conuts number of '/' in the url.
    	    
    	    //If title contains company name and url lenght is less than 50 chars.
    	    if(title.toLowerCase().contains(companyName.toLowerCase())&&(count<5)&&url.length()<50)
    	    {
    	    	
    	    	//If url has 3 '/' i.e. it's home page url.
    	    	if(count==3)
    	    	{
    	    		url = removeHttp(url);		//Removes http:// https:// etc.
    	    	    
    	    	    delta = correction(url,linkCount);	//Gives priority to .net .co etc. over other nation urls (like .it. in. etc) by decreasing distance by 1.
    	    	    //System.out.println("Distance : "+delta);
    	    	    int tmp = delta+url.length()+linkCount;
    	    	    //if(linkCount==1) tmp /= 2;
    	    		urlHashMap.put(url,tmp);	//Puts url with distance value in the hash map.
    	    		//System.out.println("URL : "+url+"  Dist : "+(delta+url.length()+linkCount));
    	    	}
    	    	//Title contains company name, but url is not home page url.
    	    	else
    	    	{
    	    		
            		try
            		{
            			//String userAgent;
            			RandomUserAgent userAgentObj = new RandomUserAgent();
        	    		String userAgent = userAgentObj.getRandomUserAgent();
            			//Connects to homepage.
            			Document doc = Jsoup.connect( homeUrl)
                						.userAgent(userAgent)
                						.referrer("http://www.google.com") 
                						.ignoreHttpErrors(true)
                						.timeout(5000).get();

                		String homeTitle = doc.title();			//Home page title
            			
                		homeTitle = homeTitle.replaceAll("\\s+","");		//Removes white space from the homepage title.
                		
                		//Home page title contains the company name.
                		if(homeTitle.toLowerCase().contains(companyName.toLowerCase()))
                		{
                			homeUrl = removeHttp(homeUrl);			//Removes http:// https:// etc.
                			delta = correction(homeUrl,linkCount);			//Calculates correction.
								                			//As home page title contains company name, So distance is 0. 
								                			//But google query returned some inner address of a website.
								                			//To give direct homepage urls some edge over this url, added url length as distance. 
                			urlHashMap.put(homeUrl,url.length()+delta+linkCount);		//Puts url with distance value in the hash map.
                		}
                		//Home page title doesn't contain the company name.
                		else
                		{
                			homeTitle = homeTitle.toLowerCase();
                											//Calculates distance between company name and homepage title.
                			int dist = stringDist(companyName,homeTitle);		
                			homeUrl = removeHttp(homeUrl);						//Removes http:// https:// etc.
                			delta = correction(homeUrl,linkCount);						//Calculates corrections.
                			urlHashMap.put(homeUrl,dist+url.length()+delta+linkCount);	//Puts url with distance value in the hash map.
                			
                		}
            		}
            		catch(Exception e)
            		{
            			//Connection error or Site is not allowing Bots.
            		}
    	    	}
        	    
        		
    	    }
    	    //Google search title doesn't contain company name, but it is home page url.
    	    else if(count==3)
    	    {
    	    	int dist = stringDist(companyName,title);		//Calculate distance between title and comapny name.
    	    	
    	    	url = removeHttp(url);							//Removes http:// https:// etc.
	    	    delta = correction(url,linkCount);
    	    	urlHashMap.put(url,dist+delta+url.length()+linkCount);	//Puts url with distance value in the hash map.				
    	    	//System.out.println("Same home page name");
    	    	continue;
    	    } 
    	    else
    	    {
    	    	//Skip every other links.
    	    	//System.out.println("****Skipping this URL****" );
    	    }
    	    linkCount++;
    	}
    	setUrlHashMap(urlHashMap);
    	List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(urlHashMap.entrySet());
        //Sorts domain name based on distance.
    	Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b)
			{
				return a.getValue().compareTo(b.getValue());
			}
		});	
    	int j=0;
    	for (Map.Entry<String, Integer> entry : entries) 
    	{
    		if(j<20)
    		{
    			result[j] = entry.getKey();
    			j++;
    		}
    		else
    			break;
    		System.out.println(entry.getKey()+" "+entry.getValue());
    	}
    	return result;
	}
	
	public String getRank(String domainName,String companyName,int numberOfUrls) throws UnsupportedEncodingException, IOException
	{
		int rank = 0;
		int distance = 0;
		String result="";
		App obj = new App();
        //Elements links = generateURLs(companyName,numberOfUrls);	//Generates google search urls.
		GoogleResults googleResultsObj = new GoogleResults();
		Elements links = getLinks();
        for (Element link : links) 								//Iterates through the links.
    	{
    	    rank++;
        	String title = link.text();
    	    String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
    	    try
    		{
	    	    url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");
	    	    if(url.contains(domainName))
	    	    {
	    	    	if(url.charAt(url.length() - 1)!='/')
		    	    	url = url + "/";
	    	    	 String homeUrl = obj.getHomePageURL(url);		//Get home page url based on google search result url.
	    	    	 GoogleResults googleObj = new GoogleResults();    
	    	    	 if(homeUrl.equals(url))
	    	    	 {
	    	    		 distance = googleObj.stringDist(companyName, title);
	    	    	 }
	    	    	 else
	    	    	 {
	    	    		 RandomUserAgent userAgentObj = new RandomUserAgent();
	     	    		String userAgent = userAgentObj.getRandomUserAgent();
	    	    		 Document doc = Jsoup.connect( homeUrl)
	     						.userAgent(userAgent)
	     						.referrer("http://www.google.com") 
	     						.ignoreHttpErrors(true)
	     						.timeout(5000).get();
	
	    	    		 		String homeTitle = doc.title();	
	    	    		 		distance = googleObj.stringDist(companyName, homeTitle);
	    	    	 }
	    	    	 result = rank + "|" + distance;
	    	    	    //System.out.println("G Url : "+url +"   Rank"+result);
	    	    	break;
	    	    }
        		
    	    }catch(Exception e)
    		{
    			//Connection error or Site is not allowing Bots.
    		}
    	}
  
		return result;
		
	}
	public void setUrlHashMap(LinkedHashMap<String, Integer> urlHashMap)
	{
		this.urlHashMap = urlHashMap;
	}
	
	public LinkedHashMap<String, Integer> getUrlHashMap()
	{
		return this.urlHashMap;
	}
	
	public void setLinks(String companyName,int numberOfUrls)
	{
		try {
			this.links = generateURLs(companyName,numberOfUrls);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	//Generates google search urls.
	}
	
	public Elements getLinks()
	{
		return this.links;
	}
	
	//Returns url based on search query.
	public static Elements generateURLs(String search, int numberOfUrl) throws UnsupportedEncodingException, IOException
	{
		String google = "http://www.google.com/search?q=";
		String charset = "UTF-8";
		String userAgent = "ExampleBot 1.0 (+http://example.com/bot)";
		RandomUserAgent userAgentObj = new RandomUserAgent();
		//String userAgent = userAgentObj.getRandomUserAgent();
		Elements links = Jsoup.connect(google + URLEncoder.encode(search, charset)+ "&num="+ numberOfUrl).userAgent(userAgent).referrer("http://www.google.com").get().select(".g>.r>a");
		return links;
	}
	
	//Calculates distance between 2 string dynamically.
	//Variant of 'Edit Distance' problem. Here, we can't remove any character from company name.
    public static int stringDist(String str1, String str2)
    {
        int[][] distance = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++)
            distance[i][0] = i;
        for (int j = 1; j <= str2.length(); j++)
            distance[0][j] = j;

        for (int i = 1; i <= str1.length(); i++)
        {
        	for (int j = 1; j <= str2.length(); j++)
        	{
        		//Can't miss any character from str1(Comapny Name), So no distance[i][j-1] term.
        		distance[i][j] = Math.min(distance[i - 1][j] + 1, distance[i - 1][j - 1] + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1));
        	}
        }
        return distance[str1.length()][str2.length()];
    }
	
	//Skips the url if url contains following keywords.
	public Boolean skipUrlFromUrl(String URL)
	{
		//If link contains this keywords, then there is no need to go to it's home page.
		if(URL.contains("/wiki/")||URL.contains("/store/")
				||URL.contains("/app/")||URL.contains("/topic/")
				||URL.contains("/news/")||URL.contains("/dictionary/")
				||URL.contains("/books?")||URL.contains("/public/")
				||URL.contains("/Reviews/")||URL.contains("/company/")
				||URL.contains("/Companies/")||URL.contains("/Salary/")
				||URL.contains("/Apps/")||URL.contains("/startups/")
				||URL.contains("/hashtag/")||URL.contains("/page/")
				||URL.contains("/2016/")||URL.contains("/2015/")
				||URL.contains("/2015/")||URL.contains("/2014/")
				||URL.contains("/user/")||URL.contains("/business/")
				||URL.contains("/stories/")||URL.contains("/tagged/")
				||URL.contains("/tool/")||URL.contains(".pdf")
				||URL.contains("/product/")||URL.contains("/jobs/")
				||URL.contains("/Jobs/")||URL.contains("/Company/")
				||URL.contains("/quote/")||URL.contains("/articles/")
				||URL.contains("/content/")||URL.contains("/package/")
				||URL.contains("/browse/")||URL.contains("/blogs/")
				||URL.contains("/node/")||URL.contains("/News/")
				||URL.contains("/start-ups/")||URL.contains("/stocks/")
				||URL.contains("/video/")||URL.contains("dictionary.com/")
				||URL.contains(".blogspot.com")||URL.contains("/support/")
				||URL.contains("/services/")||URL.contains("/company/")
				||URL.contains("/Profile/")||URL.contains("/books?")
				||URL.contains("/public/")||URL.contains("/books?")
				
				||URL.contains("/post/")||URL.contains(".wordpress.com")
				||URL.contains("/tag/")||URL.contains("/Places/")||URL.contains("/category/")||URL.contains("/quotes/"))
			return true;
		else
			return false;
	}
	
	//Skips the url if title contains following keywords.
	public Boolean skipUrlFromTitle(String title)
	{
		//If title contains this keywords, then there is no need to go to it's home page.
		//It will be just a page of that company on some social media sites.
		if(title.contains("| Twitter")||title.contains("- Facebook")
				||title.contains(" Instagram photos and videos")||title.contains("- Wikipedia, the free encyclopedia")
				||title.contains("- wikiHow")||title.contains("- Quora")
				||title.contains("- Forbes")||title.contains("| LinkedIn")
				||title.contains("- YouTube")||title.contains("- Google+")
				||title.contains("- AngelList")||title.contains("| AngelList")
				||title.contains("- Product Hunt")||title.contains("| StackShare")
				||title.contains("| Hasjob")||title.contains("· GitHub")
				||title.contains("- YourStory")||title.contains("| Flickr")
				||title.contains("on Tumblr")||title.contains("| Glassdoor")
				||title.contains("on Vimeo")||title.contains("- Yahoo")
				||title.contains("- NASDAQ.com")||title.contains("- Reddit")
				||title.contains("| TechCrunch")||title.contains("- Mashable")
				||title.contains("- Naukri.com")||title.contains("on reddit.com")				
				||title.contains("- WordPress.com")||title.contains("| CrunchBase")
				||title.contains("on Pinterest |")||title.contains("| CrunchBase")
				||title.contains("| PayScale")||title.contains("| CrunchBase")
				)
			return true;
		else
			return false;
	}
	
	//Removes http:// https:// etc.
	public String removeHttp(String URL)
	{
		URL = URL.replace("http://","");
		URL = URL.replace("https://","");
		URL = URL.replace("/","");
	    return URL;
	}
	
	//Removes ltd. Inc. etc. from company name.
	public String modifyCompanyName(String companyName)
	{
		companyName = companyName.toLowerCase();
	    companyName = companyName.replace(",","");
	    companyName = companyName.replace("&","");
	    companyName = companyName.replace(" Inc","");
	    companyName = companyName.replace(".","");
	    companyName = companyName.replace("pvt","");
	    companyName = companyName.replace("ltd","");
	    companyName = companyName.replace("financial","");
	    companyName = companyName.replace("corporation","");
	    companyName = companyName.replaceAll("\\s+","");
	    return companyName;
	}
	
	
	
	
    //Gives priority to international domain names like .net .co .com etc over nation domain name like .in .it. etc.
    public int correction(String urlString,int linkCount)
    {
    	int corr = 0;
    	String url = urlString.replace("www.", "");
    	if(url.contains(".co")||url.contains(".net")||url.contains(".org")||url.contains(".io")||url.contains(".edu")||url.contains(".ac"))
    		corr--;
    	if(linkCount==1)
    	{
    		corr = corr - 5;
    		System.out.println("FIrstLink :"+urlString+" | Corr : "+corr);
    	}
    	return corr;
    }
	
}