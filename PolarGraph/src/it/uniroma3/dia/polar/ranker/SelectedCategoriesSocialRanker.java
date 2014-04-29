package it.uniroma3.dia.polar.ranker;

import it.uniroma3.dia.polar.comparator.RecommendedObjectComparatorByScoreDesc;
import it.uniroma3.dia.polar.graph.model.Couple;
import it.uniroma3.dia.polar.graph.model.PolarPlace;
import it.uniroma3.dia.polar.graph.model.RecommendedObject;
import it.uniroma3.dia.polar.persistance.CypherRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

/**
 * First extracts all the places visited by friends and under a certain Category.
 * It gives a score according to the number of friends that visited it.
 * */
public class SelectedCategoriesSocialRanker extends Ranker{
	
	private final CypherRepository repository;
	
	@Inject
	public SelectedCategoriesSocialRanker(final CypherRepository repository){
		this.repository = repository;
	}
	
	
	@Override
	public List<RecommendedObject> recommendObject(String userId) {
		this.repository.startDB();
		Map<String, Couple<PolarPlace,Long>> place2visitors = this.repository.findPlacesVisitedByTheUserAndCountFriends(userId);
		List<Couple<PolarPlace, Long>> placesAndVisitorsByTouristAttractionCategory = this.repository.findPlacesBySingleCategoryName(userId, "Tourist Attraction"); //Tourist Attraction is very good

		this.repository.stopDB();
		//normalize the scores
		List<Double> scores = new ArrayList<Double>();
		for(Couple<PolarPlace, Long> couple : placesAndVisitorsByTouristAttractionCategory){
			Long visitors = couple.getSecond();
			
			scores.add(visitors.doubleValue());
		}
		Double maxScore = 0d;
		if(scores!=null && scores.size() >0){
			 maxScore = Collections.max(scores);
		}
		//the normalized score is the number of visitors divided by the maxscore
		
		//create the ranked places list
		List<RecommendedObject> rankedPlaces = new ArrayList<RecommendedObject>();
		for(Couple<PolarPlace, Long> couple : placesAndVisitorsByTouristAttractionCategory){
			RecommendedObject rankedPlace = convertToRecommendedObject(couple.getFirst());
			Long visitors = couple.getSecond();
			double normalizedScore = visitors.doubleValue() / maxScore;
			rankedPlace.setScore(normalizedScore);
			rankedPlaces.add(rankedPlace);
		}
		
		//sort the list according to the score
		Collections.sort(rankedPlaces, new RecommendedObjectComparatorByScoreDesc());
		return rankedPlaces;
	}	
}
