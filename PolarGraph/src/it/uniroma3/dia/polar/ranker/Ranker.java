package it.uniroma3.dia.polar.ranker;

import it.uniroma3.dia.polar.graph.model.PolarPlace;
import it.uniroma3.dia.polar.graph.model.RecommendedObject;

import java.util.List;

public class Ranker {

	public List<RecommendedObject> recommendObject(String userId){
		return null;
	}
	
	/***
	 * Converts from a place to a RecommendedObject 
	 */
	public RecommendedObject convertToRecommendedObject(PolarPlace place){
		RecommendedObject rankedPlace = new RecommendedObject();
		rankedPlace.setId(place.getId());
		rankedPlace.setName(place.getName());
		rankedPlace.setUri(place.getUri());

		return rankedPlace;
	}
	
	@Deprecated
	public RankedPlace convertToRankedPlace(PolarPlace place){
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
}
