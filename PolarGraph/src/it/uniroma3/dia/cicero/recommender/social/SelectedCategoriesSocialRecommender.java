package it.uniroma3.dia.cicero.recommender.social;

import it.uniroma3.dia.cicero.comparator.RecommendedObjectComparatorByScoreDesc;
import it.uniroma3.dia.cicero.graph.model.Couple;
import it.uniroma3.dia.cicero.graph.model.PolarPlace;
import it.uniroma3.dia.cicero.graph.model.RecommendedObject;
import it.uniroma3.dia.cicero.persistance.CypherRepository;
import it.uniroma3.dia.cicero.recommender.Recommender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

/**
 * First extracts all the places visited by friends and under a certain
 * Category. It gives a score according to the number of friends that visited
 * it.
 * */
public class SelectedCategoriesSocialRecommender extends AbstractSelectedCategoriesSocialRecommender {

	private CypherRepository repository;

	@Inject
	public SelectedCategoriesSocialRecommender(CypherRepository repository) {
		super(repository);	
	}

	@Override
	public List<RecommendedObject> recommendObject(String userId, List<RecommendedObject> inputObjects) {
		this.getRepository().startDB();
		if (this.getCategories().isEmpty()) { // if it is empty add default
												// categories
			this.getCategories().add("Monument");
			this.getCategories().add("Tourist Attraction");
			this.getCategories().add("Museum");
		}
		List<Couple<PolarPlace, Long>> placesAndVisitorsByTouristAttractionCategory = queryTheRepository(userId);
		this.getRepository().stopDB();
		// normalize the scores
		List<Double> scores = new ArrayList<Double>();
		for (Couple<PolarPlace, Long> couple : placesAndVisitorsByTouristAttractionCategory) {
			Long visitors = couple.getSecond();

			scores.add(visitors.doubleValue());
		}
		Double maxScore = 0d;
		if (scores != null && scores.size() > 0) {
			maxScore = Collections.max(scores);
		}
		// the normalized score is the number of visitors divided by the
		// maxscore

		// create the ranked places list
		List<RecommendedObject> rankedPlaces = new ArrayList<RecommendedObject>();
		for (Couple<PolarPlace, Long> couple : placesAndVisitorsByTouristAttractionCategory) {
			RecommendedObject rankedPlace = convertToRecommendedObject(couple.getFirst());
			Long visitors = couple.getSecond();
			double normalizedScore = visitors.doubleValue() / maxScore;
			rankedPlace.setScore(normalizedScore);
			rankedPlaces.add(rankedPlace);
		}

		// sort the list according to the score
		Collections.sort(rankedPlaces, new RecommendedObjectComparatorByScoreDesc());
		return rankedPlaces;
	}

	public List<Couple<PolarPlace, Long>> queryTheRepository(String userId) {
		return this.getRepository().findPlacesByMultiplesCategoryNames(userId, this.getCategories());
	}

}
