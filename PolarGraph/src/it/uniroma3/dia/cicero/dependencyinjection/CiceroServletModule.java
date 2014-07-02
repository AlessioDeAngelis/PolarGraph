package it.uniroma3.dia.cicero.dependencyinjection;

import it.uniroma3.dia.cicero.controller.CiceroFacade;
import it.uniroma3.dia.cicero.controller.PropertiesManager;
import it.uniroma3.dia.cicero.controller.RecommenderChainManager;
import it.uniroma3.dia.cicero.disambiguator.Disambiguator;
import it.uniroma3.dia.cicero.disambiguator.NaiveDisambiguator;
import it.uniroma3.dia.cicero.disambiguator.SemanticPlacesDisambiguator;
import it.uniroma3.dia.cicero.parser.JSONParser;
import it.uniroma3.dia.cicero.parser.XMLParser;
import it.uniroma3.dia.cicero.persistance.CypherRepository;
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

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;

/**
 * To be used with servlet
 * */
public class CiceroServletModule extends AbstractModule {

	private final static Logger logger = LoggerFactory
			.getLogger(CiceroServletModule.class);
	private final ServletContext context;
	
	private  RankerType rankerType;

	public CiceroServletModule(final ServletContext context, RankerType rankerType) {
		this.context = context;
		this.rankerType = rankerType;
	}
	
	public CiceroServletModule(final ServletContext context) {
		this.context = context;
		this.rankerType = RankerType.NAIVE;
	}

	/***
	 * In this method I will configure the module in order to let it work with
	 * the guice dependency injection method
	 */
	@Override
	protected void configure() {
		install(new ServletModule());
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
		bind(RecommenderChainManager.class).in(Singleton.class);

		bind(RecommenderFactory.class).in(Singleton.class);

		bind(SemanticBaseRecommender.class).in(Singleton.class);
		bind(SemanticCleverRecommender.class).in(Singleton.class);
		bind(EuropeanaRecommender.class).in(Singleton.class);
		bind(SemanticCloserPlacesRecommender.class).in(Singleton.class);

		bind(NaiveSocialRecommender.class).in(Singleton.class);
		bind(SelectedCategoriesSocialRecommender.class).in(Singleton.class);
		bind(SelectedCategoriesCollaborativeFilteringSocialRecommender.class).in(Singleton.class);
	}
	
	

	private void bindProperties() {
		Properties properties = new Properties();
		String path = "data/polar_graph.properties";
		try {
			properties.load(new FileReader(context.getRealPath("/") + path));
			// this will bind the properties to guice
			Names.bindProperties(binder(), properties);
		} catch (FileNotFoundException e) {
			logger.error("The configuration file for the properties can not be found "
					+ context.getRealPath("/") + path);
		} catch (IOException e) {
			logger.error("I/O Exception during loading configuration");
		}
	}

}
