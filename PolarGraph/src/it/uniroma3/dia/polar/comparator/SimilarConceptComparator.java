package it.uniroma3.dia.polar.comparator;

import it.uniroma3.dia.polar.graph.model.SimilarConcept;
import java.util.Comparator;

public class SimilarConceptComparator implements Comparator<SimilarConcept> {

	@Override
	public int compare(SimilarConcept o1, SimilarConcept o2) {
		int result = 0;
		double diff = o1.getSimilarity() - o2.getSimilarity();
		if (diff > 0) {
			result = -1;
		} else if (diff < 0) {
			result = 1;
		}
		return result;
	}
}