package it.uniroma3.dia.cicero.dependencyinjection;

import it.uniroma3.dia.cicero.recommender.Recommender;
import it.uniroma3.dia.cicero.recommender.semantic.EuropeanaRecommender;
import it.uniroma3.dia.cicero.recommender.semantic.SemanticBaseRecommender;
import it.uniroma3.dia.cicero.recommender.semantic.SemanticCleverRecommender;
import it.uniroma3.dia.cicero.recommender.semantic.SemanticCloserPlacesRecommender;
import it.uniroma3.dia.cicero.recommender.social.NaiveSocialRecommender;
import it.uniroma3.dia.cicero.recommender.social.RandomSocialRecommender;
import it.uniroma3.dia.cicero.recommender.social.SelectedCategoriesCollaborativeFilteringSocialRecommender;
import it.uniroma3.dia.cicero.recommender.social.SelectedCategoriesSocialRecommender;

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
		case "randomsocial":
			recommender = injector.getInstance(RandomSocialRecommender.class);
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
