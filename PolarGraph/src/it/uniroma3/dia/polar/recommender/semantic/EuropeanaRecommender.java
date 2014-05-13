package it.uniroma3.dia.polar.recommender.semantic;

import it.uniroma3.dia.polar.graph.model.RecommendedObject;
import it.uniroma3.dia.polar.persistance.CypherRepository;
import it.uniroma3.dia.polar.rdf.JenaManager;
import it.uniroma3.dia.polar.recommender.Recommender;
import it.uniroma3.dia.polar.recommender.social.SelectedCategoriesCollaborativeFilteringSocialRecommender;
import it.uniroma3.dia.polar.recommender.social.SelectedCategoriesSocialRecommender;

import java.util.ArrayList;
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
	public List<RecommendedObject> recommendObject(String userId) {
		List<RecommendedObject> rankedPlaces = socialRecommender.recommendObject(userId);
		List<RecommendedObject> recommendedObjects = new ArrayList<RecommendedObject>();
		int i = 0;
		while (recommendedObjects.size() <= 0 && i < rankedPlaces.size()) {
			String term = rankedPlaces.get(i).getName();
			// TODO:this method should return europenaa objects instead
			recommendedObjects.addAll(jenaManager.queryEuropeana(term));
			System.out.println(term + " " + recommendedObjects.size());

			i++;
		}

		return recommendedObjects;
	}
}
