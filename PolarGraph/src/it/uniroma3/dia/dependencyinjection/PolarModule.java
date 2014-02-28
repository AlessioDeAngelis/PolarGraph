package it.uniroma3.dia.dependencyinjection;

import it.uniroma3.dia.polar.persistance.CypherRepository;
import it.uniroma3.dia.polar.persistance.FacebookRepository;
import it.uniroma3.polar.PolarMain;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

public class PolarModule extends AbstractModule {

	private final static Logger logger = LoggerFactory.getLogger(PolarModule.class);

	
	public PolarModule() {

	}

	/***
	 * In this method I will configure the module in order to let it work with
	 * the guice dependency injection method
	 */
	@Override
	protected void configure() {
		bindProperties();
        bind(FacebookRepository.class).in(Singleton.class);
        bind(CypherRepository.class).in(Singleton.class);
	}
	
	private void bindProperties(){
		Properties properties = new Properties();
		try {
	        properties.load(new FileReader("data/polar_graph.properties"));
	        //this will bind the properties to guice
	        Names.bindProperties(binder(), properties);
	    } catch (FileNotFoundException e) {
	        logger.error("The configuration file Test.properties can not be found");
	    } catch (IOException e) {
	        logger.error("I/O Exception during loading configuration");
	    }
	}

}
