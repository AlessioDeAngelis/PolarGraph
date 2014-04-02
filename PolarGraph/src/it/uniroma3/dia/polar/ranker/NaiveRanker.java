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

public class NaiveRanker implements Ranker{
	
	private final CypherRepository repository;
	
	@Inject
	public NaiveRanker(final CypherRepository repository){
		this.repository = repository;
	}
	
	/**
	 * The naive extract first all the places that the user visited together with the category and the number of friends that visited it
	 * and assigns a score that is calculated by dividing the maximum number of visitors among all places by the actual number of visitors of the current place
	 * */
	@Override
	public List<RecommendedObject> recommendObject(String userId) {
		this.repository.startDB();
		Map<String, Couple<PolarPlace,Long>> place2visitors = this.repository.findPlacesVisitedByTheUserAndCountFriends(userId);
		this.repository.stopDB();
		//normalize the scores
		List<Double> scores = new ArrayList<Double>();
		for(Couple<PolarPlace, Long> couple : place2visitors.values()){
			Long visitors = couple.getSecond();
			
			scores.add(visitors.doubleValue());
		}
		Double maxScore = Collections.max(scores);
		//the normalized score is the number of visitors divided by the maxscore
		
		//create the ranked places list
		List<RecommendedObject> rankedPlaces = new ArrayList<RecommendedObject>();
		for(Couple<PolarPlace, Long> couple : place2visitors.values()){
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
	
	@Deprecated
	private RankedPlace convertToRankedPlace(PolarPlace place){
		RankedPlace rankedPlace = new RankedPlace();
		rankedPlace.setId(place.getId());
		rankedPlace.setName(place.getName());
		rankedPlace.setUri(place.getUri());
		rankedPlace.setLocation(place.getLocation());
		rankedPlace.setLikesCount(place.getLikesCount());
		rankedPlace.setCategories(place.getCategories());
		rankedPlace.setLikedBy(place.getLikedBy());
		return rankedPlace;
	}
	
	private RecommendedObject convertToRecommendedObject(PolarPlace place){
		RecommendedObject rankedPlace = new RecommendedObject();
		rankedPlace.setId(place.getId());
		rankedPlace.setName(place.getName());
		rankedPlace.setUri(place.getUri());

		return rankedPlace;
	}
}
