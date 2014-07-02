package it.uniroma3.dia.cicero.dependencyinjection;

import it.uniroma3.dia.cicero.controller.CategoriesManager;
import it.uniroma3.dia.cicero.controller.CiceroFacade;
import it.uniroma3.dia.cicero.controller.PropertiesManager;
import it.uniroma3.dia.cicero.disambiguator.Disambiguator;
import it.uniroma3.dia.cicero.disambiguator.SemanticPlacesDisambiguator;
import it.uniroma3.dia.cicero.parser.JSONParser;
import it.uniroma3.dia.cicero.parser.XMLParser;
import it.uniroma3.dia.cicero.persistance.CypherRepository;
import it.uniroma3.dia.cicero.persistance.EvaluationRepository;
import it.uniroma3.dia.cicero.persistance.FacebookRepository;
import it.uniroma3.dia.cicero.rdf.JenaManager;
import it.uniroma3.dia.cicero.recommender.Recommender;
import it.uniroma3.dia.cicero.recommender.semantic.EuropeanaRecommender;
import it.uniroma3.dia.cicero.recommender.semantic.SemanticBaseRecommender;
import it.uniroma3.dia.cicero.recommender.semantic.SemanticCleverRecommender;
import it.uniroma3.dia.cicero.recommender.semantic.SemanticCloserPlacesRecommender;
import it.uniroma3.dia.cicero.recommender.social.NaiveSocialRecommender;
import it.uniroma3.dia.cicero.recommender.social.SelectedCategoriesCollaborativeFilteringSocialRecommender;
import it.uniroma3.dia.cicero.recommender.social.SelectedCategoriesSocialRecommender;
import it.uniroma3.dia.cicero.rest.RestManager;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

/**
 * To use without servlet
 * */
public class CiceroModule extends AbstractModule {

	private final static Logger logger = LoggerFactory
			.getLogger(CiceroModule.class);

	public CiceroModule() {

	}

	/***
	 * In this method I will configure the module in order to let it work with
	 * the guice dependency injection method
	 */
	@Override
	protected void configure() {
//		install(new ServletModule()); //TODO: uncomment it if you are using servlet
		bindProperties();
		bind(CiceroFacade.class).in(Singleton.class);
		bind(FacebookRepository.class).in(Singleton.class);
		bind(CypherRepository.class).in(Singleton.class);
		bind(RestManager.class).in(Singleton.class);
		bind(Disambiguator.class).to(SemanticPlacesDisambiguator.class).in(
				Singleton.class);
		// bind(Ranker.class).to(SemanticBaseRanker.class).in(Singleton.class);
//		bind(Recommender.class).to(NaiveSocialRecommender.class).in(Singleton.class);
		bind(Recommender.class).to(SelectedCategoriesSocialRecommender.class).in(Singleton.class);
		bind(JenaManager.class).in(Singleton.class);
		bind(PropertiesManager.class).in(Singleton.class);
		bind(XMLParser.class).in(Singleton.class);
		bind(JSONParser.class).in(Singleton.class);
		
		bind(RecommenderFactory.class).in(Singleton.class);

		bind(SemanticBaseRecommender.class).in(Singleton.class);
		bind(SemanticCleverRecommender.class).in(Singleton.class);
		bind(EuropeanaRecommender.class).in(Singleton.class);
		bind(SemanticCloserPlacesRecommender.class).in(Singleton.class);

		bind(NaiveSocialRecommender.class).in(Singleton.class);
		bind(SelectedCategoriesSocialRecommender.class).in(Singleton.class);
		bind(SelectedCategoriesCollaborativeFilteringSocialRecommender.class).in(Singleton.class);

		bind(CategoriesManager.class).in(Singleton.class);
		bind(EvaluationRepository.class).in(Singleton.class);
//		bind(String.class)
//	    .annotatedWith(Names.named("access_token"))
//	    .toProvider(Providers.<String>of(null));
//		bind(String.class)
//	    .annotatedWith(Names.named("db_path"))
//	    .toProvider(Providers.<String>of(null));
	}

	private void bindProperties() {
		Properties properties = new Properties();
		try {
			String path = "data/polar_graph.properties";
	
			properties.load(new FileReader(path));
			// this will bind the properties to guice
			Names.bindProperties(binder(), properties);
		} catch (FileNotFoundException e) {
			logger.error("The configuration file for the properties can not be found");
		} catch (IOException e) {
			logger.error("I/O Exception during loading configuration");
		}
	}

}
