package it.uniroma3.dia.dependencyinjection;

import it.uniroma3.dia.polar.controller.PolarFacade;
import it.uniroma3.dia.polar.controller.PropertiesManager;
import it.uniroma3.dia.polar.disambiguator.Disambiguator;
import it.uniroma3.dia.polar.disambiguator.JSONParser;
import it.uniroma3.dia.polar.disambiguator.NaiveDisambiguator;
import it.uniroma3.dia.polar.disambiguator.XMLParser;
import it.uniroma3.dia.polar.persistance.CypherRepository;
import it.uniroma3.dia.polar.persistance.FacebookRepository;
import it.uniroma3.dia.polar.ranker.NaiveSocialRanker;
import it.uniroma3.dia.polar.ranker.Ranker;
import it.uniroma3.dia.polar.rdf.JenaManager;
import it.uniroma3.dia.polar.rest.RestManager;

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
public class PolarServletModule extends AbstractModule {

	private final static Logger logger = LoggerFactory
			.getLogger(PolarServletModule.class);
	private final ServletContext context;
	
	private  RankerType rankerType;

	public PolarServletModule(final ServletContext context, RankerType rankerType) {
		this.context = context;
		this.rankerType = rankerType;
	}
	
	public PolarServletModule(final ServletContext context) {
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
		bind(PolarFacade.class).in(Singleton.class);
		bind(FacebookRepository.class).in(Singleton.class);
		bind(CypherRepository.class).in(Singleton.class);
		bind(RestManager.class).in(Singleton.class);
		bind(Disambiguator.class).to(NaiveDisambiguator.class).in(
				Singleton.class);
//		bind(Ranker.class).to(SemanticBaseRanker.class).in(Singleton.class);
//		 bind(Ranker.class).to(NaiveRanker.class).in(Singleton.class);
//		// bind(NaiveRanker.class).in(Singleton.class);
		bind(JenaManager.class).in(Singleton.class);
		bind(PropertiesManager.class).in(Singleton.class);
		bind(XMLParser.class).in(Singleton.class);
		bind(JSONParser.class).in(Singleton.class);
		// bind(String.class)
		// .annotatedWith(Names.named("access_token"))
		// .toProvider(Providers.<String>of(null));
		// bind(String.class)
		// .annotatedWith(Names.named("db_path"))
		// .toProvider(Providers.<String>of(null));
		bind(RankerType.class).toInstance(rankerType);
		bind(Ranker.class).toProvider(RankerProvider.class).in(Singleton.class);
//		bind(Ranker.class).in(Singleton.class);
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
