package it.uniroma3.dia.cicero.recommender.semantic;

import it.uniroma3.dia.cicero.comparator.EuropeanaRecommendedObjectComparatorByMediaUrl;
import it.uniroma3.dia.cicero.graph.model.RecommendedObject;
import it.uniroma3.dia.cicero.persistance.CypherRepository;
import it.uniroma3.dia.cicero.rdf.JenaManager;
import it.uniroma3.dia.cicero.recommender.Recommender;
import it.uniroma3.dia.cicero.recommender.social.SelectedCategoriesCollaborativeFilteringSocialRecommender;
import it.uniroma3.dia.cicero.recommender.social.SelectedCategoriesSocialRecommender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.inject.Inject;

public class EuropeanaRecommender extends Recommender {

	 private final SelectedCategoriesCollaborativeFilteringSocialRecommender
	 socialRecommender;
	private final CypherRepository repository;
	private final JenaManager jenaManager;
//	private final SemanticCloserPlacesRecommender socialRecommender;

	@Inject
	public EuropeanaRecommender(final CypherRepository repository,
	 final SelectedCategoriesCollaborativeFilteringSocialRecommender
	 socialRecommender,
//			final SemanticCloserPlacesRecommender socialRecommender,
	 final JenaManager jenaManager) {
		this.repository = repository;
		this.jenaManager = jenaManager;
		this.socialRecommender = socialRecommender;
	}

	@Override
	public List<RecommendedObject> recommendObject(String userId, List<RecommendedObject> inputObjects) {
//		socialRecommender.setCategories(this.getCategories());
//		List<RecommendedObject> inputObjects = socialRecommender.recommendObject(userId);
		List<RecommendedObject> recommendedObjects = new ArrayList<RecommendedObject>();
		int i = 0;
		while (recommendedObjects.size() <= 12 && i < inputObjects.size()) {
			String term = inputObjects.get(i).getName();
			List<RecommendedObject> tmp = jenaManager.queryEuropeana(term);
			Collections.sort(tmp,new EuropeanaRecommendedObjectComparatorByMediaUrl());
			
			//give less result in order to improve the diversity
			if(tmp.size()>3){
				tmp = tmp.subList(0, 3);
			}

			recommendedObjects.addAll(tmp);
			System.out.println(term + " " + recommendedObjects.size());
			i++;
		}
		//sort the object: with this strategy. The first result must be the one with a pciture, so that the user is more satisfied
		Collections.sort(recommendedObjects,new EuropeanaRecommendedObjectComparatorByMediaUrl());

		return recommendedObjects;
	}
}
