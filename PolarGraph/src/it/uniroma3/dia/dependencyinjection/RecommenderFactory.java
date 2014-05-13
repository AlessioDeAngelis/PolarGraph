package it.uniroma3.dia.dependencyinjection;

import it.uniroma3.dia.polar.recommender.Recommender;
import it.uniroma3.dia.polar.recommender.semantic.EuropeanaRecommender;
import it.uniroma3.dia.polar.recommender.semantic.SemanticBaseRecommender;
import it.uniroma3.dia.polar.recommender.semantic.SemanticCleverRecommender;
import it.uniroma3.dia.polar.recommender.semantic.SemanticCloserPlacesRecommender;
import it.uniroma3.dia.polar.recommender.social.NaiveSocialRecommender;
import it.uniroma3.dia.polar.recommender.social.SelectedCategoriesCollaborativeFilteringSocialRecommender;
import it.uniroma3.dia.polar.recommender.social.SelectedCategoriesSocialRecommender;

import com.google.inject.Injector;

/**
 * The factory for Recommenders. It is used insted of Guice Providers because I
 * need to to create at runtime the type of recommender that I need
 * */
public class RecommenderFactory {

	public Recommender createRecommender(String recommenderName, Injector injector) {
		Recommender recommender = null;
		switch (recommenderName) {
		case "naive":
			recommender = injector.getInstance(NaiveSocialRecommender.class);
			break;

		case "semanticbase":
			recommender = injector.getInstance(SemanticBaseRecommender.class);
			break;
		case "selected_categories_social":
			recommender = injector.getInstance(SelectedCategoriesSocialRecommender.class);
			break;
		case "selected_categories_social_collaborative_filtering":
			recommender = injector.getInstance(SelectedCategoriesCollaborativeFilteringSocialRecommender.class);
			break;
		case "semanticclever":
			recommender = injector.getInstance(SemanticCleverRecommender.class);
			break;
		case "semantic_closer_places":
			recommender = injector.getInstance(SemanticCloserPlacesRecommender.class);
			break;
		case "europeana":
			recommender = injector.getInstance(EuropeanaRecommender.class);
			break;
		default:
			recommender = injector.getInstance(NaiveSocialRecommender.class);
			break;

		}
		return recommender;

	}
}
