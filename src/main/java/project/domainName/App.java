package project.domainName;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
/**
 * Hello world!
 *
 */
public class App 
{	
	private static Pattern patternDomainName;
	private Matcher matcher;
	private static final String DOMAIN_NAME_PATTERN 
		= "([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}";
	static {
		patternDomainName = Pattern.compile(DOMAIN_NAME_PATTERN);
	}
	  
		
	
	
	
	
	
    public static void main( String[] args ) throws UnsupportedEncodingException, IOException
    {
    	Scanner in = new Scanner(System.in);
        String companyName;
        System.out.println("Enter a Company name :");
        companyName = in.nextLine();
        while(companyName!=""&&companyName.length()>1)
        {
        	System.out.println("Searching domain name of \'"+companyName+"\'");
        	String search = companyName;
            
            trustCertificates();
            
            GoogleResults google = new GoogleResults();
            String[] t = google.googleDomainName(companyName, 5); 
            System.out.println("Top results from google:\n" );
            for(int i=0;i<5;i++)
            {
            	System.out.println("" +t[i]);
            }
            
            WikipediaResults wiki = new WikipediaResults();
            String t1 = wiki.wikiDomainName(search,3);
            System.out.println("From Wikipedia : \n" +t1);
            AngelListResults angelList = new AngelListResults();
            String t2 = angelList.angelListDomainName(search, 4);
            System.out.println("From Angel List: \n " +t2);
            
            companyName ="";
            System.out.println("Enter a Company name :");
            companyName = in.nextLine();
        }
        
     }
    
    public static void trustCertificates()
    {
    	 TrustManager[] trustAllCerts = new TrustManager[]{
    	    new X509TrustManager()
    	    {
                public java.security.cert.X509Certificate[] getAcceptedIssuers()
                {
                    return null;
                }
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                {
                    //No need to implement.
                }
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                {
                    //No need to implement.
                }
            }
    	 };

        // Install the all-trusting trust manager
        try 
        {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } 
        catch (Exception e) 
        {
            System.out.println(e);
        }
    }
    
    public String getHomePageURL(String url)
    {
    	int i = 0, pos = 0, tpos = 0;
    	String temp = url;
    	pos = temp.indexOf("//");
    	tpos += pos +2;
    	temp = temp.substring(pos+2);
    	pos = temp.indexOf('/');
    	tpos += pos;
    	//System.out.println(pos);
    	return url.substring(0, tpos);
    	
    }
    
    public String getDomainName(String url)
    {
		
    	String domainName = "";
    	matcher = patternDomainName.matcher(url);
    	if (matcher.find()) {
    		domainName = matcher.group(0).toLowerCase().trim();
    	}
    	return domainName;
    		
     }
}    