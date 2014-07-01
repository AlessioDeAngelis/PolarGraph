package it.uniroma3.dia.cicero.dependencyinjection;

import it.uniroma3.dia.cicero.controller.CiceroFacade;
import it.uniroma3.dia.cicero.controller.PropertiesManager;
import it.uniroma3.dia.cicero.disambiguator.Disambiguator;
import it.uniroma3.dia.cicero.disambiguator.NaiveDisambiguator;
import it.uniroma3.dia.cicero.parser.JSONParser;
import it.uniroma3.dia.cicero.parser.XMLParser;
import it.uniroma3.dia.cicero.persistance.CypherRepository;
import it.uniroma3.dia.cicero.persistance.FacebookRepository;
import it.uniroma3.dia.cicero.rdf.JenaManager;
import it.uniroma3.dia.cicero.recommender.Recommender;
import it.uniroma3.dia.cicero.recommender.semantic.SemanticBaseRecommender;
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
 * For adding the bindings for ranker at runtime
 * */
//public class TemporaryRankerModule extends AbstractModule {
//
//	private final static Logger logger = LoggerFactory
//			.getLogger(TemporaryRankerModule.class);
//	private final ServletContext context;
//	
//	private  RankerType rankerType;
//
//	public TemporaryRankerModule(final ServletContext context, RankerType rankerType) {
//		this.context = context;
//		this.rankerType = rankerType;
//	}
//	
//	public TemporaryRankerModule(final ServletContext context) {
//		this.context = context;
//		this.rankerType = RankerType.NAIVE;
//	}
//
//	/***
//	 * In this method I will configure the module in order to let it work with
//	 * the guice dependency injection method
//	 */
//	@Override
//	protected void configure() {
//		
//		bind(RankerType.class).toInstance(rankerType);
//		bind(Recommender.class).toProvider(RankerProvider.class).in(Singleton.class);
//	}
//}
