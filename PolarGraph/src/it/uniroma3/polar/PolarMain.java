package it.uniroma3.polar;

import it.uniroma3.dia.dependencyinjection.PolarModule;
import it.uniroma3.dia.polar.controller.PolarController;
import it.uniroma3.dia.polar.controller.PropertiesController;
import it.uniroma3.dia.polar.persistance.CypherRepository;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class PolarMain {

	private final static Logger logger = LoggerFactory.getLogger(PolarMain.class);
//TODO: only one injector invocation
	public static void main(String[] args) {

		long start = System.currentTimeMillis();
		logger.info("Start");
		storeMyInfo();
//		readAllNodesOfAType("Category");
		logger.info("End in " + (System.currentTimeMillis() - start) + " msec");
	}

	public static Properties loadProperties() {
		Properties prop = new Properties();
		Injector injector = Guice.createInjector(new PolarModule());
		PropertiesController propertiesController = injector.getInstance(PropertiesController.class);
		prop = propertiesController.getProperties();
		return prop;
	}

	public static void readAllNodesOfAType(String nodeType) {
		Properties props = loadProperties();

		String fbUserId = props.getProperty("fb_user_id");
		Injector injector = Guice.createInjector(new PolarModule());
		CypherRepository repo = injector.getInstance(CypherRepository.class);
		repo.startDB();
		repo.findAllNodesByType(nodeType);
		repo.stopDB();

	}

	public static void countCommonVisitors() {
		Properties props = loadProperties();
		String fbUserId = props.getProperty("fb_user_id");
		Injector injector = Guice.createInjector(new PolarModule());
		CypherRepository repo = injector.getInstance(CypherRepository.class);
		repo.startDB();
		repo.countFriendsThatVisitedSimilarPlaces(fbUserId);
		repo.stopDB();
	}

	public static void queryLike() {
		Properties props = loadProperties();
		String fbUserId = props.getProperty("fb_user_id");
		Injector injector = Guice.createInjector(new PolarModule());
		CypherRepository repo = injector.getInstance(CypherRepository.class);
		repo.startDB();
		repo.countFriendsThatVisitedSimilarPlaces(fbUserId);
		repo.stopDB();
	}

	public static void storeMyInfo() {
		Properties props = loadProperties();

		String fbUserId = props.getProperty("fb_user_id");

		Injector injector = Guice.createInjector(new PolarModule());
		PolarController polarController = injector.getInstance(PolarController.class);

		 polarController.readUserFromFacebookAndStore(fbUserId);
		polarController.readVisitedPlacesFromFacebookAndStore(fbUserId);
		// polarController.readFriendsFromFacebookAndStore(fbUserId);

		// polarController.readPlacesVisitedByFriendsAndStore(fbUserId);
		polarController.readPlacesTaggedInPhotoAndStore(fbUserId);
		// polarController.readPlacesTaggedInPhotoByFriendsAndStore(fbUserId);

	}
}
