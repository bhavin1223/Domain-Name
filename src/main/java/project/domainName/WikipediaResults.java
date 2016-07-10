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
	
	//Returns the domain-name (if found) from Wikipedia website.
	public String wikiDomainName (String search,int numberOfUrlr) throws UnsupportedEncodingException, IOException
	{
    	search = search + " wikipedia";

    	Elements links = generateURLs(search,numberOfUrlr);	//Returns top 4 urls google search query.
		String result = "";
    	
		for (Element link : links) 
    	{
    		String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
    	    url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

    	    if (!url.startsWith("http")||!url.contains(".wikipedia.")) {
    	    	System.out.println("~~" + url);
    	    	continue; // Ads/news/etc.
    	    }
    	    System.out.println("!~" + url);
			Document doc;
	    	try 
	    	{
	    		
	    		RandomUserAgent userAgentObj = new RandomUserAgent();
	    		String userAgent = userAgentObj.getRandomUserAgent();
	    		// need http protocol
	    		doc = Jsoup.connect(url).userAgent(userAgent).timeout(5000).get();
	    	//	System.out.println("Doc = "+doc);
//	    		while(doc==null)
//	    		{
//	    			System.out.println("Trying to reconnect.."+doc);
//	    			doc = Jsoup.connect(url).userAgent(userAgent).timeout(5000).get();
//	    		}
//	    		// get page title
	    		String title = doc.title();					//Page title
	    		System.out.println("title : " + title);
	
	    		
	    		Element elements = doc.select("table.infobox").first();			//Selects infobox table
	    		
	    			Iterator<Element> iterator = elements.select("td").iterator();
	    			Iterator<Element> iterator1 = elements.select("th").iterator();
	    			String temp2="";
	    			while(iterator.hasNext())									//Finds website url
			        {
			        	String temp = iterator.next().text();
//			        	
//			        	System.out.println("Th : "+temp2 +" | Td: " +temp);
//			        	if(temp2.contains("Website"))
//			        	{
//			        		//temp = iterator.next().text();
//			        		result = temp;
//			        		//break;
//			        	}
//			        	 temp2 = iterator1.next().text();
			        	if(!temp.contains("(")&&!temp.contains("[")&&temp.contains(".")&&!temp.contains(" ")&&temp.length()>4&&!temp.contains("$")&&!temp.contains(",")&&temp.charAt(temp.length()-1)!='.')
			            {
			        		//Domain name found.
			        		result = temp;
			        		break;
			            }
			        	
			        	//System.out.println("Th: "+temp);
			        }
	    		
	    	} catch (Exception e) {
	    		//Error in connection.
	    		e.printStackTrace();
	    	}
	    	 break;
    	}
    	return result;
	}
	
	//Returns url based on search query.
	public static Elements generateURLs(String search, int numberOfUrl) throws UnsupportedEncodingException, IOException
	{
		String google = "http://www.google.com/search?q=";
		String charset = "UTF-8";
		System.out.println("In genUrl fn : "+search);
		//String userAgent = "ExampleBot 1.0 (+http://example.com/bot)";
		RandomUserAgent userAgentObj = new RandomUserAgent();
		String userAgent = userAgentObj.getRandomUserAgent();
		Elements links = Jsoup.connect(google + URLEncoder.encode(search, charset)+ "&num="+ numberOfUrl).userAgent(userAgent).timeout(5000).referrer("http://www.google.com").get().select(".g>.r>a");
		int cnt=0;
		while(links.isEmpty()==true&&cnt<5)
		{
			userAgent = userAgentObj.getRandomUserAgent();
			links = Jsoup.connect(google + URLEncoder.encode(search, charset)+ "&num="+ numberOfUrl).userAgent(userAgent).timeout(5000).referrer("http://www.google.com").get().select(".g>.r>a");
			cnt++;
		}
		System.out.println("In genUrl fn : "+links);
		return links;
	}
}
