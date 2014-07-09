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
public class SelectedCategoriesSocialRecommender extends
		AbstractSelectedCategoriesSocialRecommender {

	private CypherRepository repository;

	@Inject
	public SelectedCategoriesSocialRecommender(CypherRepository repository) {
		super(repository);
	}

	@Override
	public List<RecommendedObject> recommendObject(String userId,
			List<RecommendedObject> inputObjects) {
		this.getRepository().startDB();
		List<RecommendedObject> outputObjects = new ArrayList<RecommendedObject>();
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
		double sumScore = 0;
		double avgScore = 0;
		// create the ranked places list
		List<RecommendedObject> rankedPlaces = new ArrayList<RecommendedObject>();
		for (Couple<PolarPlace, Long> couple : placesAndVisitorsByTouristAttractionCategory) {
			RecommendedObject rankedPlace = convertToRecommendedObject(couple
					.getFirst());
			Long visitors = couple.getSecond();
			double normalizedScore = visitors.doubleValue() / maxScore;
			sumScore += normalizedScore;
			rankedPlace.setScore(normalizedScore);
			rankedPlaces.add(rankedPlace);
		}
		avgScore = sumScore / rankedPlaces.size();
		if (rankedPlaces.size() > 10) {
			for (int i = 0; i < rankedPlaces.size(); i++) {
				if (rankedPlaces.get(i).getScore() > avgScore) {
					outputObjects.add(rankedPlaces.get(i));
				}
			}
		}

		// sort the list according to the score
		Collections.sort(outputObjects,
				new RecommendedObjectComparatorByScoreDesc());
		if (outputObjects.size() > 10) {
			outputObjects = outputObjects.subList(0, 10);
		}
		return outputObjects;
	}

	public List<Couple<PolarPlace, Long>> queryTheRepository(String userId) {
		List<Couple<PolarPlace, Long>> result = this.getRepository().findPlacesByMultiplesCategoryNames(userId,
				this.getCategories());
		//if the previuous result is null, do another query with less constraints
		if(result == null || result.size() ==0 ){
 			result = this.getRepository().findPlacesVisitedOnlyByFriends(userId, this.getCategories());
 		}
		return result;
	}

}
