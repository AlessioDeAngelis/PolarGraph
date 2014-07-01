package it.uniroma3.dia.cicero.comparator;

import it.uniroma3.dia.cicero.graph.model.RankedPlace;
import it.uniroma3.dia.cicero.graph.model.RecommendedObject;

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
