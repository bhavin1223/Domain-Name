package project.domainName;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrunchBaseResults {
	
	//Returns the domain-name (if found) from crunchBase website.
	public static String crunchBaseDomainName (String search,int numberOfUrls) throws UnsupportedEncodingException, IOException
	{

		String result = "";
    	search = search + " CrunchBase";
    	Elements links = generateURLs(search,numberOfUrls);	//Returns top 4 urls google search query.
    	for (Element link : links) 
    	{
    		String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
    	    url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

    	    if (!url.startsWith("http")||!url.contains("crunchbase.com")) {
    	    	System.out.println("~" + url);
    	    	continue; // Ads/news/etc.
    	    }
    	    
    	    System.out.println("" + url);
			Document doc;
			RandomUserAgent userAgentObj = new RandomUserAgent();
    		//String userAgent = userAgentObj.getRandomUserAgent();
			//String userAgent = "ExampleBot 1.0 (+http://example.com/bot)";
    		String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36"; 
    		doc = Jsoup.connect(url).ignoreHttpErrors(true).userAgent(userAgent).followRedirects(true).referrer("http://www.google.com").timeout(30000).get();		// need http protocol
//    		while(doc==null)
//    		{
//    			System.out.println("Trying to reconnec CrunchBase..");
//    			doc = Jsoup.connect(url).userAgent(userAgent).timeout(5000).get();
//    		}
    		String title = doc.title();				//Page title
    		//System.out.println("doc " + doc +"  title : " + title);
    		
	    	try 
	    	{
	    		Elements listItems = doc.getElementsByTag("a");
	    		for(Element item: listItems)			//Finds the domain-name in listItems.
	    		{
	    			String temp = item.text();
	    			if(temp.contains("http")&&temp.contains(".")&&!temp.contains(" "))
	    			{
	    				//Domain-name found.
	    				//System.out.println(temp+"--From CrunchBase");
	    				temp = removeHttp(temp);
	    				temp = temp.toLowerCase();
	    				result = temp;
	    				App obj = new App();
	    				result = obj.getHomePageURL(temp);	
	    				break;
	    			}
	    			
	    		}
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    	 break;
    	}
    	return result;
	}
	
	//Returns url based on search query.
	public static Elements generateURLs(String search, int numberOfUrls) throws UnsupportedEncodingException, IOException
	{
		String google = "http://www.google.com/search?q=";
		String charset = "UTF-8";
		//String userAgent = "ExampleBot 1.0 (+http://example.com/bot)";
		RandomUserAgent userAgentObj = new RandomUserAgent();
		String userAgent = userAgentObj.getRandomUserAgent();
		Elements links = Jsoup.connect(google + URLEncoder.encode(search, charset)+ "&num="+ numberOfUrls).userAgent(userAgent).referrer("http://www.google.com").get().select(".g>.r>a");
//		while(links.isEmpty()==true)
//		{
//			userAgent = userAgentObj.getRandomUserAgent();
//			links = Jsoup.connect(google + URLEncoder.encode(search, charset)+ "&num="+ numberOfUrls).userAgent(userAgent).timeout(5000).referrer("http://www.google.com").get().select(".g>.r>a");
//		}
		//System.out.println("In genUrl fn : "+links);
		return links;
	}
	
	//Removes http:// , https:// etc from Url.
	public static String removeHttp(String URL)
	{
		URL = URL.replace("http://","");
		URL = URL.replace("https://","");
		URL = URL.replace("www.","");
		URL = URL.replace("/","");
	    return URL;
	}
}
