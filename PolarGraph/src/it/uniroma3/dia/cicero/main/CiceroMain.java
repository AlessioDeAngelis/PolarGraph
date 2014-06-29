package it.uniroma3.dia.cicero.main;

import it.uniroma3.dia.cicero.controller.CiceroFacade;
import it.uniroma3.dia.cicero.controller.PropertiesManager;
import it.uniroma3.dia.cicero.dependencyinjection.CiceroModule;
import it.uniroma3.dia.cicero.graph.model.Couple;
import it.uniroma3.dia.cicero.graph.model.PolarPlace;
import it.uniroma3.dia.cicero.graph.model.RecommendedObject;
import it.uniroma3.dia.cicero.persistance.CypherRepository;
import it.uniroma3.dia.cicero.recommender.semantic.SemanticCleverRecommender;
import it.uniroma3.dia.cicero.recommender.social.SelectedCategoriesSocialRecommender;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class CiceroMain {

	private final static Logger logger = LoggerFactory.getLogger(CiceroMain.class);

	// TODO: only one injector invocation
	public static void main(String[] args) {

		long start = System.currentTimeMillis();
		logger.info("Start");
		// recommendPlace();
		// storeMyInfo();
		// storeMyFriendsInfo();
		// readAllNodesOfAType("Category");
		// placeCategories();
		storeMyPosts();
		logger.info("End in " + (System.currentTimeMillis() - start) + " msec");
	}

	public static Properties loadProperties() {
		Properties prop = new Properties();
		Injector injector = Guice.createInjector(new CiceroModule());
		PropertiesManager propertiesController = injector.getInstance(PropertiesManager.class);
		prop = propertiesController.getProperties("data/polar_graph.properties");
		return prop;
	}

	public static void readAllNodesOfAType(String nodeType) {
		Properties props = loadProperties();

		String fbUserId = props.getProperty("fb_user_id");
		Injector injector = Guice.createInjector(new CiceroModule());
		CypherRepository repo = injector.getInstance(CypherRepository.class);
		repo.startDB();
		repo.printAllNodesByType(nodeType);
		repo.stopDB();

	}

	public static void countCommonVisitors() {
		Properties props = loadProperties();
		String fbUserId = props.getProperty("fb_user_id");
		Injector injector = Guice.createInjector(new CiceroModule());
		CypherRepository repo = injector.getInstance(CypherRepository.class);
		repo.startDB();
		repo.countFriendsThatVisitedSimilarPlaces(fbUserId);
		repo.stopDB();
	}

	public static void queryLike() {
		Properties props = loadProperties();
		String fbUserId = props.getProperty("fb_user_id");
		Injector injector = Guice.createInjector(new CiceroModule());
		CypherRepository repo = injector.getInstance(CypherRepository.class);
		repo.startDB();
		repo.countFriendsThatVisitedSimilarPlaces(fbUserId);
		repo.stopDB();
	}

	public static void storeMyInfo() {
		Properties props = loadProperties();

		String fbUserId = props.getProperty("fb_user_id");

		Injector injector = Guice.createInjector(new CiceroModule());
		CiceroFacade polarController = injector.getInstance(CiceroFacade.class);
		//
		polarController.readUserFromFacebookAndStore(fbUserId);
//		polarController.readVisitedPlacesFromFacebookAndStore(fbUserId);
//		polarController.readPlacesTaggedInPhotoAndStore(fbUserId);

		// polarController.readFriendsFromFacebookAndStore(fbUserId);

	}

	public static void storeMyFriendsInfo() {
		Properties props = loadProperties();

		String fbUserId = props.getProperty("fb_user_id");

		Injector injector = Guice.createInjector(new CiceroModule());
		CiceroFacade polarController = injector.getInstance(CiceroFacade.class);

		polarController.readPlacesVisitedByFriendsAndStore(fbUserId);
		polarController.readPlacesTaggedInPhotoByFriendsAndStore(fbUserId);

	}

	public static void recommendPlace() {
		Properties props = loadProperties();

		String fbUserId = props.getProperty("fb_user_id");

		Injector injector = Guice.createInjector(new CiceroModule());
		CiceroFacade polarController = injector.getInstance(CiceroFacade.class);
		polarController.recommendPlace(fbUserId);
	}

	public static void placeCategories() {
		Properties props = loadProperties();

		String fbUserId = props.getProperty("fb_user_id");

		Injector injector = Guice.createInjector(new CiceroModule());

		// SelectedCategoriesSocialRanker ranker =
		// injector.getInstance(SelectedCategoriesSocialRanker.class);
		// List<RecommendedObject> obs = ranker.recommendObject(fbUserId);
		// for(RecommendedObject o : obs){
		// logger.debug(o.getUri() + ", " +o.getScore());
		// }
		SemanticCleverRecommender ranker = injector.getInstance(SemanticCleverRecommender.class);
		ranker.recommendObject(fbUserId,null);
	}

	public static void storeMyPosts() {
		Properties props = loadProperties();

		String fbUserId = props.getProperty("fb_user_id");

		Injector injector = Guice.createInjector(new CiceroModule());
		CypherRepository repo = injector.getInstance(CypherRepository.class);
		CiceroFacade polarController = injector.getInstance(CiceroFacade.class);
		repo.startDB();
		polarController.readUserPostsAndStore(fbUserId);
		repo.stopDB();
	}
	
	

}
