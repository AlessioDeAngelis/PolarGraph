package it.uniroma3.dia.polar.comparator;

import it.uniroma3.dia.polar.graph.model.RecommendedObject;
import it.uniroma3.dia.polar.ranker.RankedPlace;

import java.util.Comparator;

public class RecommendedObjectComparatorByScoreDesc implements Comparator<RecommendedObject> {

	@Override
	public int compare(RecommendedObject o1, RecommendedObject o2) {
		int result = 0;
		double diff = o1.getScore() - o2.getScore();
		if (diff > 0) {
			result = -1;
		} else if (diff < 0) {
			result = 1;
		}
		return result;
	}

}
