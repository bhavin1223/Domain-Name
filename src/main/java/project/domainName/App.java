package project.domainName;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import au.com.bytecode.opencsv.CSVReader;

import com.google.gson.Gson;
import com.opencsv.CSVWriter;

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

        String[] row = null;
        String csvFilename = "E:\\test-data.csv";
        String predictedDomain ="";
        CSVReader csvReader = new CSVReader(new FileReader(csvFilename));
        List content = csvReader.readAll();
        String [] CompanyName  		= new String[305];
        String [] DomainName  		= new String[305];
        String [] PredictedDomain 	= new String[305];
        String [] WikiDomain  		= new String[305];
        String [] AngelListDomain  	= new String[305];
        String [] CrunchBaseDomain  = new String[305];
        String [] GoogleSearchRank  = new String[305];
        String [] GoogleDomain 		= new String[305];
        String [] Distance  		= new String[305];
        String [] TotalUrls  		= new String[305];
        String [] RankInResults		= new String[305];
        int cnt=0;
        for (Object object : content) {
        	row = (String[]) object;
        	CompanyName[cnt] = row[0];
        	DomainName[cnt] = row[1];
        	//PredictedDomain[cnt] = row[2];
        	System.out.println(row[0]
        	           + " # " + row[1]
        	          );
        	cnt++;
        }
        //...
        csvReader.close();
    	
    	String csv = "E:\\output.csv";
    	CSVWriter writer = new CSVWriter(new FileWriter(csv));

    	List<String[]> data = new ArrayList<String[]>();
    	data.add(new String[] {"CompanyName","DomainName","PredictedDomain","WikiDomain","AngelListDomain","CrunchBaseDomain","GoogleDomain","GoogleSearchRank","Distance","TotalUrls","RankInResults"});

    	
    	int start,end;
    	System.out.println("Enter the starting index :");
    	start = in.nextInt();
    	System.out.println("Enter the ending index :");
    	end = in.nextInt();
    	trustCertificates();
    	App appObj = new App();
        //companyName = in.nextLine();
        //while(companyName!=""&&companyName.length()>1)
        for(int index=start;index<=end;index++)
        {
        	companyName = CompanyName[index];
        	System.out.println("Searching the domain name of \'"+companyName+"\'");
        	String search = companyName;
        	RankInResults[index] = "1";
        	if(appObj.isDomainName(companyName))
        	{
        		
        		PredictedDomain[index] = companyName;
        		GoogleSearchRank[index] = "1";
        		Distance[index] = "0";
        		TotalUrls[index] = "1";
        		WikiDomain[index] = "";
        		AngelListDomain[index] ="";
        		CrunchBaseDomain[index] ="";
        		GoogleDomain[index] = "";
        		RankInResults[index] = "1";
        		data.add(new String[] {CompanyName[index],DomainName[index],PredictedDomain[index],WikiDomain[index],AngelListDomain[index],CrunchBaseDomain[index],GoogleDomain[index],GoogleSearchRank[index],Distance[index],TotalUrls[index],RankInResults[index]});
        		System.out.println("CompanyName : "+CompanyName[index] + "\nDomainName : "+DomainName[index]+"\nPredictedDomain : "+PredictedDomain[index]);
                System.out.println("WikiDomain : "+WikiDomain[index] + "\nAngelListDomain : "+AngelListDomain[index]+"\nCrunchBaseDomain : "+CrunchBaseDomain[index]+"\nGoogleDomain : "+GoogleDomain[index]);
                System.out.println("GoogleSearchRank : "+GoogleSearchRank[index] + "\nDistance : "+Distance[index]+"\nTotalUrls : "+TotalUrls[index]+"\nRankInResults : "+RankInResults[index]);
        		continue;
        	}
            
            GoogleResults google = new GoogleResults();
            
            //Gives best 20 matches from top 50 google search results.
            String[] googleDomain = new String[20];
            googleDomain = google.googleDomainName(companyName, 50); 
              
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
            //crunchBaseDomain = crunchBase.crunchBaseDomainName(search,4);
            
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
            		predictedDomain = crunchBaseDomain;
            		System.out.println(crunchBaseDomain);
            		flag = 1;
            	}
            	else if(crunchBaseDomain.equals(angelListDomain)&&angelListDomain.length()>4)
            	{
            		predictedDomain = crunchBaseDomain;
            		System.out.println(crunchBaseDomain);
            		flag = 1;
            	}
            	else if(angelListDomain.equals(wikiDomain)&&wikiDomain.length()>4)
            	{
            		predictedDomain = wikiDomain;
            		System.out.println(wikiDomain);
            		flag = 1;
            	}
            	else
	            	for(int i=0;i<Math.min(url_cnt, 20);i++)
	                {
	                	if((wikiDomain.length()>1)&&(googleDomain[i].equals(wikiDomain)==true))
	                	{
	                		predictedDomain = googleDomain[i];
//	                		RankInResults[index] = ""+ (i+1);
	                		System.out.println("" + googleDomain[i]);
	                		flag = 1;
	                		break;
	                	}
	                	else if(angelListDomain.length()>1&&
	                			(googleDomain[i].equals(angelListDomain)==true))
	                	{
	                		predictedDomain = googleDomain[i];
//	                		RankInResults[index] = ""+ (i+1);
	                		System.out.println("" + googleDomain[i]);
	                		flag = 1;
	                		break;
	                	}
	                	else if(crunchBaseDomain.length()>1&&
	                			(googleDomain[i].equals(crunchBaseDomain)==true))
	                	{
	                		predictedDomain = googleDomain[i];
//	                		RankInResults[index] = ""+ (i+1);
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
            
            GoogleDomain[index] = googleDomain[0];
            //If no match found the print all the domain names.
            if(flag==0)
            {
            	predictedDomain = googleDomain[0];
            	for(int i=0;i<Math.min(url_cnt,5);i++)
            		System.out.println(googleDomain[i]);
            	if(wikiDomain.length()>1)
            		System.out.println(wikiDomain);
            	if(angelListDomain.length()>1)
            		System.out.println(angelListDomain);
            	if(crunchBaseDomain.length()>1)
            		System.out.println(crunchBaseDomain);
            }
            TotalUrls[index] = ""+ url_cnt;
            PredictedDomain[index] = predictedDomain;
	        //GoogleRank googleRankObj = new GoogleRank();
	      	String result = "";
	      	result =google.getRank(PredictedDomain[index],CompanyName[index], 50);
	      	if(result.contains("|"))
	      	{
	      		GoogleSearchRank[index] = result.substring(0, result.indexOf('|'));
	      		Distance[index] = result.substring(result.indexOf('|')+1);
	      	}
	      	LinkedHashMap<String, Integer> urlHashMap = google.getUrlHashMap();
	      	List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(urlHashMap.entrySet());
	      	int cnt1=1;
	      	for (Map.Entry<String, Integer> entry : entries) 
	    	{
	      		
	    		if(entry.getKey().contains(predictedDomain))
	    		{
	    			RankInResults[index] = "" +cnt1;break;
	    		}
	    		cnt1++;
	    	}
            WikiDomain[index] =wikiDomain;
            AngelListDomain[index] = angelListDomain;
            CrunchBaseDomain[index] = crunchBaseDomain;
            PredictionScore predictionScoreObj = new PredictionScore();
            Double score = predictionScoreObj.calculateScore(PredictedDomain[index], GoogleSearchRank[index], Distance[index], TotalUrls[index], RankInResults[index], WikiDomain[index], AngelListDomain[index], CrunchBaseDomain[index]);
            data.add(new String[] {CompanyName[index],DomainName[index],PredictedDomain[index],WikiDomain[index],AngelListDomain[index],CrunchBaseDomain[index],GoogleDomain[index],GoogleSearchRank[index],Distance[index],TotalUrls[index],RankInResults[index]});
            System.out.println("CompanyName : "+CompanyName[index] + "\nDomainName : "+DomainName[index]+"\nPredictedDomain : "+PredictedDomain[index]);
            System.out.println("WikiDomain : "+WikiDomain[index] + "\nAngelListDomain : "+AngelListDomain[index]+"\nCrunchBaseDomain : "+CrunchBaseDomain[index]+"\nGoogleDomain : "+GoogleDomain[index]);
            System.out.println("GoogleSearchRank : "+GoogleSearchRank[index] + "\nDistance : "+Distance[index]+"\nTotalUrls : "+TotalUrls[index]+"\nRankInResults : "+RankInResults[index]+"\nPrediction Score : "+score);
            
            companyName ="";
            System.out.println("\n\nEnter a Company name :"+index);
            //companyName = in.nextLine();
        }
        writer.writeAll(data);

    	writer.close();
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
    
    public Boolean isDomainName(String companyName)
    {
    	if(!companyName.contains(" ")&&!companyName.contains(",")&&companyName.contains(".")&&companyName.charAt(companyName.length()-1)!='.')
    		return true;
    	return false;
    }
    
}    