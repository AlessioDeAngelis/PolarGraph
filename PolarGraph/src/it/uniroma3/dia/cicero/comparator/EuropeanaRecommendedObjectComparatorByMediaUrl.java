package it.uniroma3.dia.cicero.comparator;

import it.uniroma3.dia.cicero.graph.model.RankedPlace;
import it.uniroma3.dia.cicero.graph.model.RecommendedObject;

import java.util.Comparator;

/**
 * Sort according to the mediaUrl type, letting the picture (.jpg, .png, .gif)  first
 * */
public class EuropeanaRecommendedObjectComparatorByMediaUrl implements Comparator<RecommendedObject> {

	@Override
	public int compare(RecommendedObject o1, RecommendedObject o2) {
		String pattern = ".*\\.gif$|.*\\.jpg$|.*\\.png";
		boolean firstMatch = false;
		boolean secondMatch = false;
		if(o1.getMediaUrl()!=null){
			firstMatch = o1.getMediaUrl().matches(pattern);
		}
		if(o2.getMediaUrl()!=null){
			secondMatch = o2.getMediaUrl().matches(pattern);
		}
		int result = 0;
		double diff = o1.getScore() - o2.getScore();
		if (firstMatch && ! secondMatch) {
			result = -1;
		} else if (!firstMatch && secondMatch) {
			result = 1;
		}else{
			result = 0;
		}
		return result;
	}

}
