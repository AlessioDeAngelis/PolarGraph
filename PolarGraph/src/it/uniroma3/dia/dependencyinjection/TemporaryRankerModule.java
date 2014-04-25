package it.uniroma3.dia.dependencyinjection;

import it.uniroma3.dia.polar.controller.PolarFacade;
import it.uniroma3.dia.polar.controller.PropertiesManager;
import it.uniroma3.dia.polar.disambiguator.Disambiguator;
import it.uniroma3.dia.polar.disambiguator.JSONParser;
import it.uniroma3.dia.polar.disambiguator.NaiveDisambiguator;
import it.uniroma3.dia.polar.disambiguator.XMLParser;
import it.uniroma3.dia.polar.persistance.CypherRepository;
import it.uniroma3.dia.polar.persistance.FacebookRepository;
import it.uniroma3.dia.polar.ranker.Ranker;
import it.uniroma3.dia.polar.ranker.SemanticBaseRanker;
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
 * For adding the bindings for ranker at runtime
 * */
public class TemporaryRankerModule extends AbstractModule {

	private final static Logger logger = LoggerFactory
			.getLogger(TemporaryRankerModule.class);
	private final ServletContext context;
	
	private  RankerType rankerType;

	public TemporaryRankerModule(final ServletContext context, RankerType rankerType) {
		this.context = context;
		this.rankerType = rankerType;
	}
	
	public TemporaryRankerModule(final ServletContext context) {
		this.context = context;
		this.rankerType = RankerType.NAIVE;
	}

	/***
	 * In this method I will configure the module in order to let it work with
	 * the guice dependency injection method
	 */
	@Override
	protected void configure() {
		
		bind(RankerType.class).toInstance(rankerType);
		bind(Ranker.class).toProvider(RankerProvider.class).in(Singleton.class);
	}
}
