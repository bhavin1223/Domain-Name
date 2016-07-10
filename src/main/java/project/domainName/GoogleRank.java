package project.domainName;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GoogleRank {
	public static String getRank(String domainName,String companyName,int numberOfUrls) throws UnsupportedEncodingException, IOException
	{
		int rank = 0;
		int distance = 0;
		String result="";
		App obj = new App();
        //Elements links = generateURLs(companyName,numberOfUrls);	//Generates google search urls.
		GoogleResults googleResultsObj = new GoogleResults();
		Elements links = googleResultsObj.getLinks();
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
	    	    	    
	    	    	break;
	    	    }
        		
    	    }catch(Exception e)
    		{
    			//Connection error or Site is not allowing Bots.
    		}
    	}
  
		return result;
		
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
}
