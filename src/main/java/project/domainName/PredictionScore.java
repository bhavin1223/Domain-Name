package project.domainName;

public class PredictionScore {
	public double calculateScore(String predictedDomain,String searchRank,String distance, String totalUrls,String RankInResults,String wiki, String angelList, String crunchBase)
	{
		Double score=1.1532531;
		int flag=0;
		if(searchRank!="")
		{
			score += Integer.parseInt(searchRank)*(-0.0297301);
		}
		else
			score += (2)*(-0.0297301);
		if(distance!="")
		{
			score += Integer.parseInt(distance)*(-0.0005898);
		}
		else
			score += (40)*(-0.0005898);
		if(totalUrls!="")
		{
			score += Integer.parseInt(totalUrls)*(-0.0343891);
		}
		if(RankInResults!="")
		{
			score += Integer.parseInt(RankInResults)*(0.0837817);
		}
		if(wiki=="")
		{
			score += (0.0120820);
		}
		else
		{
			if(predictedDomain.contains(wiki))
			{
				flag++;
				score += (0.8049933);
			}
		}
		if(angelList=="")
		{
			score -= 0.2082345;
		}
		else
		{
			if(predictedDomain.contains(angelList))
			{
				flag++;
				score += 0.4186699;
			}
		}
		if(crunchBase=="")
		{
			score -= 0.1206941;
		}
		else
		{
			if(predictedDomain.contains(crunchBase))
			{
				flag++;
				score += 0.2377399;
			}
		}
		
		if(flag==1)
		{
			score -= -0.7025525;
		}
		if(flag==2)
		{
			score -= -0.6061722;
		}
		
		score += 0.44;
		score = score/1.4;
		if(score>1) score= 1.00;
		return score;
	}
}
