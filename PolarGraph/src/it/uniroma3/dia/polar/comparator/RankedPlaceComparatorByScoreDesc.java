package it.uniroma3.dia.polar.comparator;

import it.uniroma3.dia.polar.ranker.RankedPlace;

import java.util.Comparator;

public class RankedPlaceComparatorByScoreDesc implements Comparator<RankedPlace> {

	@Override
	public int compare(RankedPlace o1, RankedPlace o2) {
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
