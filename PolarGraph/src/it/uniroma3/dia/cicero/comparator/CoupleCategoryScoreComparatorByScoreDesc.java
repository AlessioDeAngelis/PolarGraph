package it.uniroma3.dia.cicero.comparator;

import it.uniroma3.dia.cicero.graph.model.Category;
import it.uniroma3.dia.cicero.graph.model.Couple;

import java.util.Comparator;

public class CoupleCategoryScoreComparatorByScoreDesc implements Comparator<Couple<Category,Double>> {

	@Override
	public int compare(Couple<Category,Double> o1, Couple<Category,Double> o2) {
		int result = 0;
		double diff = o1.getSecond() - o2.getSecond();
		if (diff > 0) {
			result = -1;
		} else if (diff < 0) {
			result = 1;
		}
		return result;
	}

}
