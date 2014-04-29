package it.uniroma3.polar.main;

import it.uniroma3.dia.dependencyinjection.PolarModule;
import it.uniroma3.dia.polar.controller.PolarFacade;
import it.uniroma3.dia.polar.controller.PropertiesManager;
import it.uniroma3.dia.polar.graph.model.Category;
import it.uniroma3.dia.polar.graph.model.Couple;
import it.uniroma3.dia.polar.persistance.CypherRepository;
import it.uniroma3.dia.polar.persistance.FacebookRepository;

import java.util.ArrayList;
import java.util.List;
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
//		recommendPlace();
//		storeMyInfo();
//		storeMyFriendsInfo();
//		readAllNodesOfAType("Category");
		placeCategories();
		logger.info("End in " + (System.currentTimeMillis() - start) + " msec");
	}

	public static Properties loadProperties() {
		Properties prop = new Properties();
		Injector injector = Guice.createInjector(new PolarModule());
		PropertiesManager propertiesController = injector.getInstance(PropertiesManager.class);
		prop = propertiesController.getProperties("data/polar_graph.properties");
		return prop;
	}

	public static void readAllNodesOfAType(String nodeType) {
		Properties props = loadProperties();

		String fbUserId = props.getProperty("fb_user_id");
		Injector injector = Guice.createInjector(new PolarModule());
		CypherRepository repo = injector.getInstance(CypherRepository.class);
		repo.startDB();
		repo.printAllNodesByType(nodeType);
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
		PolarFacade polarController = injector.getInstance(PolarFacade.class);
//
		 polarController.readUserFromFacebookAndStore(fbUserId);
		polarController.readVisitedPlacesFromFacebookAndStore(fbUserId);
		polarController.readPlacesTaggedInPhotoAndStore(fbUserId);

		 polarController.readFriendsFromFacebookAndStore(fbUserId);

	}
	
	public static void storeMyFriendsInfo() {
		Properties props = loadProperties();

		String fbUserId = props.getProperty("fb_user_id");

		Injector injector = Guice.createInjector(new PolarModule());
		PolarFacade polarController = injector.getInstance(PolarFacade.class);
		
		 polarController.readPlacesVisitedByFriendsAndStore(fbUserId);
		 polarController.readPlacesTaggedInPhotoByFriendsAndStore(fbUserId);

	}
	
	public static void recommendPlace(){
		Properties props = loadProperties();

		String fbUserId = props.getProperty("fb_user_id");

		Injector injector = Guice.createInjector(new PolarModule());
		PolarFacade polarController = injector.getInstance(PolarFacade.class);
		polarController.recommendPlace(fbUserId);
	}
	
	public static void placeCategories(){
		Properties props = loadProperties();

		String fbUserId = props.getProperty("fb_user_id");

		Injector injector = Guice.createInjector(new PolarModule());
		CypherRepository r = injector.getInstance(CypherRepository.class);
		r.startDB();
//		List<Couple<Category, Long>> categoriesCount = r.findPlacesVisitedByTheUserAndCountCategories(fbUserId);
//		r.findPlacesBySingleCategoryName(fbUserId, "Church"); //Tourist Attraction is very good
		List<String> categories = new ArrayList<String>();
		categories.add("Monument");
		categories.add("Tourist Attraction");
		categories.add("Museum");
		r.findPlacesByMultiplesCategoryNames(fbUserId, categories);
		
		r.stopDB();
	}
	
}
