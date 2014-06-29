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
public class SelectedCategoriesCollaborativeFilteringSocialRecommender extends AbstractSelectedCategoriesSocialRecommender {

	private  CypherRepository repository;
	
	@Inject
	public SelectedCategoriesCollaborativeFilteringSocialRecommender(CypherRepository repository) {
		super(repository);
	}		
	
	@Override
	public List<RecommendedObject> recommendObject(String userId, List<RecommendedObject> inputObjects) {
		return super.recommendObject(userId, inputObjects);
	}
	
	@Override
	public List<Couple<PolarPlace, Long>> queryTheRepository(String userId){
		return this.getRepository().findPlacesByMultiplesCategoryNamesCollaborativeFiltering(userId, this.getCategories());
	}

}
