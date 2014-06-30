package it.uniroma3.dia.cicero.controller;

import it.uniroma3.dia.cicero.disambiguator.Disambiguator;
import it.uniroma3.dia.cicero.disambiguator.SpottedPlace;
import it.uniroma3.dia.cicero.graph.model.Category;
import it.uniroma3.dia.cicero.graph.model.Couple;
import it.uniroma3.dia.cicero.graph.model.Person;
import it.uniroma3.dia.cicero.graph.model.PolarPlace;
import it.uniroma3.dia.cicero.graph.model.RecommendedObject;
import it.uniroma3.dia.cicero.parser.JSONParser;
import it.uniroma3.dia.cicero.persistance.CypherRepository;
import it.uniroma3.dia.cicero.persistance.FacebookRepository;
import it.uniroma3.dia.cicero.recommender.Recommender;
import it.uniroma3.dia.cicero.recommender.RecommenderChainManager;
import it.uniroma3.dia.cicero.rest.RestManager;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class CiceroFacade {

	private final Logger logger = LoggerFactory.getLogger(CiceroFacade.class);
	// TODO: don't hardcode string parameters!!!
	private final FacebookRepository facebookRepository;
	private final CypherRepository cypherRepository;
	private final PropertiesManager propertiesController;
	private final RestManager restManager;
	private final JSONParser jsonParser;
	private final String ACCESS_TOKEN = "";
	private final String DB_PATH = "";
	private final Disambiguator disambiguator;
	private final RecommenderChainManager recommenderChainManager;
	private final CategoriesManager categoryManager;
	private Person currentPerson;
	private Recommender recommender;

	@Inject
	public CiceroFacade(final FacebookRepository facebookRepository, final CypherRepository cypherRepository,
			final PropertiesManager propertiesController, final CategoriesManager categoriesManager, final Disambiguator disambiguator,
			final RestManager restManager, final JSONParser jsonParser, final RecommenderChainManager recommenderChainManager, final String accessToken, final String dbPath) {
		this.facebookRepository = facebookRepository;
		this.cypherRepository = cypherRepository;
		this.propertiesController = propertiesController;
		this.restManager = restManager;
		this.jsonParser = jsonParser;
		this.disambiguator = disambiguator;
		this.categoryManager = categoriesManager;
		this.recommenderChainManager = recommenderChainManager;
		this.currentPerson = new Person();
	}

	public Recommender getRecommender() {
		return recommender;
	}

	public void setRecommender(Recommender recommender) {
		this.recommender = recommender;
	}

	public void readUserFromFacebookAndStore(String fbUserId) {
		Person person = this.facebookRepository.retrievePersonByUserId(fbUserId);
		this.currentPerson = person;
		this.cypherRepository.startDB();
		this.cypherRepository.insertPersonNode(person);
		this.cypherRepository.stopDB();
	}

	public void readFriendsFromFacebookAndStore(String fbUserId) {
		Person person = this.facebookRepository.retrievePersonByUserId(fbUserId);
		List<Person> friends = this.facebookRepository.retrieveFriendsByUserId(fbUserId);
		// person.setFriends(friends);
		this.currentPerson = person;
		this.cypherRepository.startDB();
		// since neo4j cannot handle to save so many nodes at one time you
		// should split the dataset in smaller ones
		int numberOfFriendsProcessed = 0;
		int numberOfNodesToStore = 100;
		while (numberOfFriendsProcessed < friends.size()) {

			if (numberOfFriendsProcessed + numberOfNodesToStore < friends.size()) {

				person.setFriends(friends.subList(numberOfFriendsProcessed, numberOfFriendsProcessed
						+ numberOfNodesToStore));

			} else {
				person.setFriends(friends.subList(numberOfFriendsProcessed, friends.size()));
			}
			numberOfFriendsProcessed += numberOfNodesToStore;

			this.cypherRepository.insertPersonAndFriends(person);
		}
		this.cypherRepository.stopDB();
	}

	public void readPlacesVisitedByFriendsAndStore(String currentFbUserId) {
		// read the facebook friend ids only if you didn't do it already
		List<String> friendsId = this.facebookRepository.retrieveFriendsId(currentFbUserId);

		for (int i = 7; i < 200; i++) {
			String friendId = friendsId.get(i);
			this.readVisitedPlacesFromFacebookAndStore(friendId);
		}
	}
	
	public void addRecommenderToTheRecommenderChainManager(Recommender recommender){
		this.recommenderChainManager.addRecommender(recommender);
	}

	@Deprecated
	public void readPlacesTaggedInPhotoByFriendsAndStore(String currentFbUserId) {
		// read the facebook friend ids only if you didn't do it already
		List<String> friendsId = this.facebookRepository.retrieveFriendsId(currentFbUserId);

		for (int i = 10; i < 40; i++) {
			String friendId = friendsId.get(i);
			this.readPlacesTaggedInPhotoAndStore(friendId);
		}
	}

	@Deprecated
	public void readPlacesTaggedInPhotoAndStore(String fbUserId) {
		List<PolarPlace> visitedPlaces = this.facebookRepository.retrieveVisitedPlacesPhotoTaggedByUserId(fbUserId);
		// place for nodes that are semantically the same
		List<PolarPlace> placesToStore = disambiguatePlaces(visitedPlaces);
		storePlaces(fbUserId, placesToStore);
	} // now you should disambiguate the places in order to have just one

	public void readVisitedPlacesFromFacebookAndStore(String fbUserId) {
		List<PolarPlace> visitedPlaces = this.facebookRepository.retrieveUserTaggedLocations(fbUserId);
		// now you should disambiguate the places in order to have just one
		// place for nodes that are semantically the same
		List<PolarPlace> placesToStore = disambiguatePlaces(visitedPlaces);
		storePlaces(fbUserId, placesToStore);
	}

	public void readUserPostsAndStore(String fbUserId) {
		// extract posts from facebook
		List<String> userPosts = this.facebookRepository.retrievePostsByUserId(fbUserId, "/posts");
		Set<SpottedPlace> spots = new HashSet();
		for (String post : userPosts) {
			try {
				// use tagMe for extracting spots from each post
				String encoded = URLEncoder.encode(post, "UTF-8");
				String parsedText = this.restManager.queryTagMeLongText(encoded, "it");
				spots.addAll(this.jsonParser.parseTagMe(parsedText, 0.5));

				parsedText = this.restManager.queryTagMeLongText(encoded, "en");
				spots.addAll(this.jsonParser.parseTagMe(parsedText, 0.5));

			} catch (Exception e) {
			}
		}
		// insert each spot in the db
		for (SpottedPlace spot : spots) {
			this.cypherRepository.mergeUserTalksAboutRelationship(fbUserId, spot.getName(), spot.getUri());
		}
	}

	public List<RecommendedObject> recommendPlace(String fbUserId) {
		// recommend a place with the strategy of the given ranker
//		List<RecommendedObject> rankedPlaces = this.recommender.recommendObject(fbUserId);
		List<RecommendedObject> rankedPlaces = this.recommenderChainManager.startRecommendationChain(fbUserId);

		for (RecommendedObject rankedPlace : rankedPlaces) {
			logger.info("Place Name: " + rankedPlace.getName() + ", uri: " + rankedPlace.getUri() + ", score: "
					+ rankedPlace.getScore() + ", mediaUrl: " + rankedPlace.getMediaUrl());
		}
		return rankedPlaces;
	}

	private List<PolarPlace> disambiguatePlaces(Collection<PolarPlace> places) {
		List<PolarPlace> placesAfterDisambiguation = new ArrayList<PolarPlace>();
		for (PolarPlace place : places) {
			if (place != null) {
				String placeNameBeforeDisambiguation = place.getName();
				PolarPlace placeAfterDisambiguation = this.disambiguator.disambiguatePlace(place);
				placesAfterDisambiguation.add(placeAfterDisambiguation);

				logger.info("Place name before: " + placeNameBeforeDisambiguation
						+ ", place name after disambiguation: " + placeAfterDisambiguation.getName() + ", uri: "
						+ (placeAfterDisambiguation.getUri() != null ? placeAfterDisambiguation.getUri() : ""));
//				logger.info("Place name before: " + place
//						+ ", place name after disambiguation: " + placeAfterDisambiguation.getName() + ", uri: "
//						+ (placeAfterDisambiguation.getUri() != null ? placeAfterDisambiguation.getUri() : ""));
			}
		}
		return placesAfterDisambiguation;
	}

	private void storePlaces(String fbUserId, List<PolarPlace> placesToStore) {
		this.cypherRepository.startDB();
		for (PolarPlace place : placesToStore) {
			this.cypherRepository.insertPlaceNode(place);
			this.cypherRepository.mergeRelationShipBetweenNodes(fbUserId, "VISITED", place.getId());
			this.cypherRepository.mergePersonLikesPlaceRelationiship(place);
		}
		this.cypherRepository.stopDB();
	}
	
	public void clearGraphDatabase(String fbUserId) {
		this.cypherRepository.startDB();
		this.cypherRepository.clearGraph();
		this.cypherRepository.stopDB();
	}
	
	public List<Category> calculateUserFavouriteCategories(String fbUserId){
		this.cypherRepository.startDB();
		List<Couple<Category,Double>> retrievedCategories = this.cypherRepository.findUserCategoriesOrderedDesc(fbUserId);
		List<Category> favouriteCategories = this.categoryManager.calculateUserFavouriteCategories(retrievedCategories, 5);
		this.cypherRepository.stopDB();
		
		return favouriteCategories;
	}

}
