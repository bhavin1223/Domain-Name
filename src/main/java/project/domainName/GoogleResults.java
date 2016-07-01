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

	public String[] googleDomainName( String companyName,int numberOfUrls) throws UnsupportedEncodingException, IOException
	{
		//String google = "http://www.google.com/search?q=";
        App obj = new App();
    	//String charset = "UTF-8";
    	//String userAgent = "ExampleBot 1.0 (+http://example.com/bot)"; 
    	Elements links = generateURLs(companyName,numberOfUrls);
		//String result = "";
    	//Elements links = Jsoup.connect(google + URLEncoder.encode(companyName, charset)+ "&num=5").userAgent(userAgent).referrer("http://www.google.com").get().select(".g>.r>a");
    	
    	LinkedHashMap<String, Integer> test = new LinkedHashMap<String, Integer>();
    	String[] result = new String[5];   	
    	int cnt = 1;
    	for (Element link : links) 
    	{
    	    String title = link.text();
    	    String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
    	    url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

    	    if (!url.startsWith("http")) {
    	        continue; // Ads/news/etc.
    	    }
    	    System.out.println("----------");
    	    System.out.println("   "+cnt+"   ");
    	    cnt++;
    	    System.out.println("Title: " + title);
    	    String homeUrl = obj.getHomePageURL(url);
    	    if(url.charAt(url.length() - 1)!='/')
    	    	url = url + "/";
    	    System.out.println("URL: " + url);
    	    
    	    title = title.replaceAll("\\s+","");
    	    title = title.toLowerCase();
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
    	    int count = url.length() - url.replace("/", "").length();
    	    
    	    
    	    if(title.toLowerCase().contains(companyName.toLowerCase())&&(count<5)&&url.length()<50)
    	    {
    	    	if(count==3)
    	    	{
    	    		System.out.println("Distance : "+0);
    	    	    url = url.replace("http://","");
    	    	    url = url.replace("https://","");
    	    	    //url = url.replace("www.","");
    	    	    url = url.replace("/","");

    	    		test.put(url,0);
    	    	}
    	    	else
    	    	{
    	    		System.out.println("Home URL: " + homeUrl);
            		try
            		{
            			Document doc = Jsoup.connect( homeUrl)
                						.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                						.referrer("http://www.google.com") 
                						.ignoreHttpErrors(true)
                						.referrer("http://www.google.com")
                						.timeout(5000).get();

                		String homeTitle = doc.title();
                		System.out.println("Home Title : " + homeTitle);
                		if(homeTitle.toLowerCase().contains(companyName.toLowerCase()))
                		{
                			System.out.println("Distance : "+homeUrl.length());
                			homeUrl = homeUrl.replace("http://","");
                			homeUrl = homeUrl.replace("https://","");
            	    	    //homeUrl = homeUrl.replace("www.","");
                			homeUrl = homeUrl.replace("/","");
                			test.put(homeUrl,homeUrl.length());
                		}
                		else
                		{
                			homeTitle = homeTitle.replaceAll("\\s+","");
                			homeTitle = homeTitle.toLowerCase();
                			int dist = stringDist(companyName,homeTitle);
                			homeUrl = homeUrl.replace("http://","");
                			homeUrl = homeUrl.replace("https://","");
            	    	    //homeUrl = homeUrl.replace("www.","");
                			homeUrl = homeUrl.replace("/","");
                			test.put(homeUrl,dist+homeUrl.length());
                			System.out.println("Distance :" + (dist+homeUrl.length()));
                		}
            		}
            		catch(Exception e)
            		{
            			
            		}
    	    	}
        	    
        		
    	    }
    	    else if(count==3)
    	    {
    	    	int dist = stringDist(companyName,title);
    	    	System.out.println("Distance :" + dist);
    	    	url = url.replace("http://","");
	    	    url = url.replace("https://","");
	    	    //url = url.replace("www.","");
	    	    url = url.replace("/","");

    	    	test.put(url,dist);
    	    	System.out.println("Same home page name");
    	    	continue;
    	    } 
    	    else
    	    {
    	    	System.out.println("****Skipping this URL****" );
    	    }
    	    
    	    
    	    System.out.println("----------");
    	}
    	
    	List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(test.entrySet());
        
    	Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b)
			{
				return a.getValue().compareTo(b.getValue());
			}
		});	
    	int j=0;
    	for (Map.Entry<String, Integer> entry : entries) 
    	{
    		//sortedMap.put(entry.getKey(), entry.getValue());
    		//System.out.println(entry.getKey()+" | " + entry.getValue());
    		if(j<5)
    		{
    			result[j] = entry.getKey();
    			j++;
    		}
    		else
    			break;
    	}
    	return result;
	}
	
	public static Elements generateURLs(String search, int numberOfUrl) throws UnsupportedEncodingException, IOException
	{
		String google = "http://www.google.com/search?q=";
		String charset = "UTF-8";
		String userAgent = "ExampleBot 1.0 (+http://example.com/bot)";
		
		Elements links = Jsoup.connect(google + URLEncoder.encode(search, charset)+ "&num="+ numberOfUrl).userAgent(userAgent).referrer("http://www.google.com").get().select(".g>.r>a");
		return links;
	}
	
	
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
        		//Can't miss any character from str1, So no distance[i][j-1] term.
        		distance[i][j] = Math.min(distance[i - 1][j] + 1, distance[i - 1][j - 1] + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1));
        	}
        }
        return distance[str1.length()][str2.length()];
    }
	
	
}