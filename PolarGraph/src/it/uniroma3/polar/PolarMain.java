package it.uniroma3.polar;

import it.uniroma3.dia.dependencyinjection.PolarModule;
import it.uniroma3.dia.polar.controller.PolarController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class PolarMain {

	private final static Logger logger = LoggerFactory.getLogger(PolarMain.class);

	public static void main(String[] args) {

		Injector injector = Guice.createInjector(new PolarModule());
	

		Properties props = loadProperties();
		String fbUserId = props.getProperty("fb_user_id");
		
		PolarController polarController = injector.getInstance(PolarController.class);
				
//				new PolarController(accessToken, dbPath);
		long start = System.currentTimeMillis();
		logger.info("Start");
		// polarController.readUserFromFacebookAndStore(fbUserId);
		polarController.readVisitedPlacesFromFacebookAndStore(fbUserId);
		// polarController.readFriendsFromFacebookAndStore(fbUserId);

		// polarController.readPlacesVisitedByFriendsAndStore(fbUserId);
		polarController.readPlacesTaggedInPhotoAndStore(fbUserId);
		// polarController.readPlacesTaggedInPhotoByFriendsAndStore(fbUserId);

		logger.info("End in " + (System.currentTimeMillis() - start) + " msec");
	}

	public static Properties loadProperties() {
		Properties prop = new Properties();
		try {
			FileInputStream fis = new FileInputStream("data/polar_graph.properties");
			prop.load(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}
}
