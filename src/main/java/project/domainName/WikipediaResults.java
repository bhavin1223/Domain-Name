package project.domainName;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikipediaResults {
	
	public String wikiDomainName (String search,int numberOfUrlr) throws UnsupportedEncodingException, IOException
	{
//		String google = "http://www.google.com/search?q=";
//		String charset = "UTF-8";
//    	String userAgent = "ExampleBot 1.0 (+http://example.com/bot)"; // Change this to your company's name and bot homepage!
    	search = search + "wikipedia";
    	//Elements links = Jsoup.connect(google + URLEncoder.encode(search, charset)+ "&num="+numberOfUrl).userAgent(userAgent).referrer("http://www.google.com").get().select(".g>.r>a");
    	Elements links = generateURLs(search,4);
		String result = "";
    	for (Element link : links) 
    	{
    		String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
    	    url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

    	    if (!url.startsWith("http")||!url.contains(".wikipedia.")) {
    	    	System.out.println("~~" + url);
    	    	continue; // Ads/news/etc.
    	    }
    	    
			Document doc;
	    	try 
	    	{
	    		// need http protocol
	    		doc = Jsoup.connect(url).get();
	
	    		// get page title
	    		String title = doc.title();
	    		System.out.println("title : " + title);
	
	    		
	    		Element elements = doc.select("table.infobox").first();
	    		Iterator<Element> iterator = elements.select("td").iterator();
		        while(iterator.hasNext())
		        {
		        	String temp = iterator.next().text();
		        	if(temp.contains(".")&&!temp.contains(" "))
		            {
		        		result = temp;
		        //		System.out.println("text : "+ temp);
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
	
	public static Elements generateURLs(String search, int numberOfUrl) throws UnsupportedEncodingException, IOException
	{
		String google = "http://www.google.com/search?q=";
		String charset = "UTF-8";
		String userAgent = "ExampleBot 1.0 (+http://example.com/bot)";
		
		Elements links = Jsoup.connect(google + URLEncoder.encode(search, charset)+ "&num="+ numberOfUrl).userAgent(userAgent).referrer("http://www.google.com").get().select(".g>.r>a");
		return links;
	}
}
