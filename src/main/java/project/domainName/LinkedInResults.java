package project.domainName;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LinkedInResults {
	public static void linkedInDomainName (String search) throws UnsupportedEncodingException, IOException
	{
//		String google = "http://www.google.com/search?q=";
//		String charset = "UTF-8";
//		String userAgent = "ExampleBot 1.0 (+http://example.com/bot)";
//    	
    	search = search + "LinkedIn";
//    	Elements links = Jsoup.connect(google + URLEncoder.encode(search, charset)+ "&num=4").userAgent(userAgent).referrer("http://www.google.com").get().select(".g>.r>a");
    	Elements links = generateURLs(search,4);
    	for (Element link : links) 
    	{
    		String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
    	    url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

    	    if (!url.startsWith("http")||!url.contains("linkedin.com")) {
    	    	System.out.println("~" + url);
    	    	continue; // Ads/news/etc.
    	    }
    	    
			Document doc;
	    	try {
	
	    		// need http protocol
	    		String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36"; // Change this to your company's name and bot homepage!
	    		doc = Jsoup.connect(url).userAgent(userAgent).get();
	
	    		// get page title
	    		String title = doc.title();
	    		System.out.println("title : " + title);

	    		Elements listItems = doc.getElementsByAttribute("website");
	    		for(Element item: listItems)
	    		    System.out.println(item.text());
	
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    	 break;
    	}
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
