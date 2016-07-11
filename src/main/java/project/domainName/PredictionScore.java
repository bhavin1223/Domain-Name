package project.domainName;

public class PredictionScore {
	public int debug=0;
	public double calculateScore(String predictedDomain,String searchRank,String distance, String totalUrls,String RankInResults,String wiki, String angelList, String crunchBase)
	{
		Double score=1.1532531;
		int flag=0;
		if(debug==1)	System.out.println("1 "+score);
		if(searchRank!="")
		{
			score += Integer.parseInt(searchRank)*(-0.0297301);
		}
		else
		{
			score += (2)*(-0.0297301);
		}
		if(debug==1)	System.out.println("2 "+score);
		if(distance!="")
		{
			score += Integer.parseInt(distance)*(-0.0005898);
		}
		else
		{
			score += (40)*(-0.0005898);
		}
		if(debug==1)	System.out.println("3 "+score);
		if(totalUrls!="")
		{
			score += Integer.parseInt(totalUrls)*(-0.0343891);
		}
		if(debug==1)	System.out.println("4 "+score);
		if(RankInResults!="")
		{
			score += Integer.parseInt(RankInResults)*(0.0837817);
		}
		if(debug==1)	System.out.println("5 "+score);
		if(wiki=="")
		{
			score += (0.0120820);
		}
		else
		{
			if(predictedDomain.equals(wiki))
			{
				flag++;
				score += (0.8049933);
				
			}
		}
		if(debug==1)	System.out.println("6 "+score);
		if(angelList=="")
		{
			score -= 0.2082345;
		}
		else
		{
			if(predictedDomain.equals(angelList))
			{
				flag++;
				score += 0.5186699;
			}
		}
		if(debug==1)	System.out.println("7 "+score);
		if(crunchBase=="")
		{
			score -= 0.1206941;System.out.print("*");
		}
		else
		{
			if(predictedDomain.equals(crunchBase))
			{
				flag++;
				score += 0.2377399;System.out.print("#");
			}
		}
		if(debug==1)	System.out.println("8 "+score);
		if(flag==1)
		{
			score -= 0.2525525;
		}
		else if(flag==2)
		{
			score -= 0.1061722;
		}
		if(debug==1)	System.out.println("9 "+score);
		
		score += 0.44;
		score = score/1.44;
		
		if(score>1) score= 1.00;
		return score;
	}
}
