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

public class App 
{	
	
    public static void main( String[] args ) throws UnsupportedEncodingException, IOException
    {
    	Scanner in = new Scanner(System.in);
        String companyName;
        System.out.println("Enter the Company name :");
        companyName = in.nextLine();
        while(companyName!=""&&companyName.length()>1)
        {
        	System.out.println("Searching the domain name of \'"+companyName+"\'");
        	String search = companyName;
            
            trustCertificates();
            
            GoogleResults google = new GoogleResults();
            
            //Gives best 20 matches from top 50 google search results.
            String[] googleDomain = google.googleDomainName(companyName, 50); 
            
            System.out.println("Top results from google:" );
            int url_cnt =0; 
            for(int i=0;i<20;i++)
            {
            	if(googleDomain[i]!=null)
            	{
            		url_cnt++;
            		googleDomain[i] = googleDomain[i].replace("www.", "");
            	}
            	if(i<5)
            	System.out.println("" +googleDomain[i]);
            }
            
            //Gives domain name from Wikipedia.
            WikipediaResults wiki = new WikipediaResults();
            String wikiDomain = "";
            wikiDomain = wiki.wikiDomainName(search,3);
            wikiDomain = wikiDomain.replace("www.", "");
            System.out.println("From Wikipedia : '" +wikiDomain+"'");
            
            //Gives domain name from AngelList.
            AngelListResults angelList = new AngelListResults();
            String angelListDomain = "";
            angelListDomain = angelList.angelListDomainName(search, 4);
            System.out.println("From AngelList: '" +angelListDomain+"'");
            
            //Gives domain name from AngelList.
            CrunchBaseResults crunchBase = new CrunchBaseResults();
            String crunchBaseDomain = "";
            crunchBaseDomain = crunchBase.crunchBaseDomainName(search,4);
            System.out.println("From CrunchBase: '" +crunchBaseDomain+"'");
            
            wikiDomain = wikiDomain.toLowerCase();
            crunchBaseDomain = crunchBaseDomain.toLowerCase();
            angelListDomain = angelListDomain.toLowerCase();
            System.out.println("\n\nTop result(s) for the domain name(s):");
            int flag = 0;
            try
            {
            	if(crunchBaseDomain.equals(wikiDomain)&&wikiDomain.length()>4)
            	{
            		System.out.println(crunchBaseDomain);
            		flag = 1;
            	}
            	else if(crunchBaseDomain.equals(angelListDomain)&&angelListDomain.length()>4)
            	{
            		System.out.println(crunchBaseDomain);
            		flag = 1;
            	}
            	else if(angelListDomain.equals(wikiDomain)&&wikiDomain.length()>4)
            	{
            		System.out.println(wikiDomain);
            		flag = 1;
            	}
            	else
	            	for(int i=0;i<Math.min(url_cnt, 20);i++)
	                {
	                	if((wikiDomain.length()>1)&&(googleDomain[i].equals(wikiDomain)==true))
	                	{
	                		System.out.println("" + googleDomain[i]);
	                		flag = 1;
	                		break;
	                	}
	                	else if(angelListDomain.length()>1&&googleDomain[i].equals(angelListDomain)==true)
	                	{
	                		System.out.println("" + googleDomain[i]);
	                		flag = 1;
	                		break;
	                	}
	                	else if(crunchBaseDomain.length()>1&&googleDomain[i].equals(crunchBaseDomain)==true)
	                	{
	                		System.out.println("" + googleDomain[i]);
	                		flag = 1;
	                		break;
	                	}
	                }
            }
            catch(Exception e)
            {
            	//Null values in DomainName variables.
            }
            
            
            //If no match found the print all the domain names.
            if(flag==0)
            {
            	
            	for(int i=0;i<Math.min(url_cnt,5);i++)
            		System.out.println(googleDomain[i]);
            	if(wikiDomain.length()>1)
            		System.out.println(wikiDomain);
            	if(angelListDomain.length()>1)
            		System.out.println(angelListDomain);
            	if(crunchBaseDomain.length()>1)
            		System.out.println(crunchBaseDomain);
            }
            
            companyName ="";
            System.out.println("\n\nEnter a Company name :");
            companyName = in.nextLine();
        }
        System.out.println("---");
     }
    
    //Installs trust certificates.
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
    
    //Returns hompage url from google search url.
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
    
    
}    