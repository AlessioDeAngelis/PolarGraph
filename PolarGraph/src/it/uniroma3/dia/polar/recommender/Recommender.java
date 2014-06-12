package it.uniroma3.dia.polar.recommender;

import it.uniroma3.dia.polar.graph.model.PolarPlace;
import it.uniroma3.dia.polar.graph.model.RankedPlace;
import it.uniroma3.dia.polar.graph.model.RecommendedObject;
import it.uniroma3.dia.polar.persistance.CypherRepository;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

public abstract class Recommender {

	private List<String> categories;

	public Recommender() {
		this.categories = new ArrayList<>();
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public abstract List<RecommendedObject> recommendObject(String userId, List<RecommendedObject> inputObjects);

	/***
	 * Converts from a place to a RecommendedObject
	 */
	public RecommendedObject convertToRecommendedObject(PolarPlace place) {
		RecommendedObject rankedPlace = new RecommendedObject();
		rankedPlace.setId(place.getId());
		rankedPlace.setName(place.getName());
		rankedPlace.setUri(place.getUri());

		return rankedPlace;
	}

	@Deprecated
	public RankedPlace convertToRankedPlace(PolarPlace place) {
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
