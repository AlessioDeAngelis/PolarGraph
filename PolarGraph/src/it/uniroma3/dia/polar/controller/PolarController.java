package it.uniroma3.dia.polar.controller;

import it.uniroma3.dia.polar.disambiguator.Disambiguator;
import it.uniroma3.dia.polar.graph.model.Person;
import it.uniroma3.dia.polar.graph.model.PolarPlace;
import it.uniroma3.dia.polar.persistance.CypherRepository;
import it.uniroma3.dia.polar.persistance.FacebookRepository;
import it.uniroma3.dia.polar.ranker.RankedPlace;
import it.uniroma3.dia.polar.ranker.Ranker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class PolarController {

	private final Logger logger = LoggerFactory
			.getLogger(PolarController.class);
	// TODO: don't hardcode string parameters!!!
	private final FacebookRepository facebookRepository;
	private final CypherRepository cypherRepository;
	private final PropertiesController propertiesController;
	private final String ACCESS_TOKEN = "";
	private final String DB_PATH = "";
	private final Disambiguator disambiguator;
	private Person currentPerson;
	private final Ranker ranker;

	@Inject
	public PolarController(final FacebookRepository facebookRepository,
			final CypherRepository cypherRepository,
			final PropertiesController propertiesController,
			final Disambiguator disambiguator, final String accessToken,
			final String dbPath, final Ranker ranker) {
		this.facebookRepository = facebookRepository;
		this.cypherRepository = cypherRepository;
		this.propertiesController = propertiesController;
		this.disambiguator = disambiguator;
		this.ranker = ranker;
		this.currentPerson = new Person();
	}

	public void readUserFromFacebookAndStore(String fbUserId) {
		Person person = this.facebookRepository
				.retrievePersonByUserId(fbUserId);
		this.currentPerson = person;
		this.cypherRepository.startDB();
		this.cypherRepository.insertPersonNode(person);
		this.cypherRepository.stopDB();
	}

	public void readFriendsFromFacebookAndStore(String fbUserId) {
		Person person = this.facebookRepository
				.retrievePersonByUserId(fbUserId);
		List<Person> friends = this.facebookRepository
				.retrieveFriendsByUserId(fbUserId);
		person.setFriends(friends);
		this.currentPerson = person;
		this.cypherRepository.startDB();
//		since neo4j cannot handle to save so many nodes at one time you should split the dataset in smaller ones
//TODO: splittare il numero di amici in più liste in modo da farlo gestire meglio e salvarli in più volte
		this.cypherRepository.insertPersonAndFriends(person);
		this.cypherRepository.stopDB();
	}

	public void readPlacesVisitedByFriendsAndStore(String currentFbUserId) {
		// read the facebook friend ids only if you didn't do it already
		List<String> friendsId = this.facebookRepository
				.retrieveFriendsId(currentFbUserId);

		for (int i = 0; i < 200; i++) {
			String friendId = friendsId.get(i);
			this.readVisitedPlacesFromFacebookAndStore(friendId);
		}
	}

	public void readPlacesTaggedInPhotoByFriendsAndStore(String currentFbUserId) {
		// read the facebook friend ids only if you didn't do it already
		List<String> friendsId = this.facebookRepository
				.retrieveFriendsId(currentFbUserId);

		for (int i = 0; i < 200; i++) {
			String friendId = friendsId.get(i);
			this.readPlacesTaggedInPhotoAndStore(friendId);
		}
	}

	public void readPlacesTaggedInPhotoAndStore(String fbUserId) {
		List<PolarPlace> visitedPlaces = this.facebookRepository
				.retrieveVisitedPlacesPhotoTaggedByUserId(fbUserId);
		// now you should disambiguate the places in order to have just one
		// place for nodes that are semantically the same
		List<PolarPlace> placesToStore = disambiguatePlaces(visitedPlaces);
		storePlaces(fbUserId, placesToStore);
	}

	public void readVisitedPlacesFromFacebookAndStore(String fbUserId) {
		List<PolarPlace> visitedPlaces = this.facebookRepository
				.retrieveVisitedPlacesByUserId(fbUserId, "/feed");

		// let's remove duplicates
		Set<PolarPlace> visitedPlacesSet = new HashSet<PolarPlace>();
		for (PolarPlace place : visitedPlaces) {
			visitedPlacesSet.add(place);
		}
		// now you should disambiguate the places in order to have just one
		// place for nodes that are semantically the same
		List<PolarPlace> placesToStore = disambiguatePlaces(visitedPlacesSet);
		storePlaces(fbUserId, placesToStore);
	}

	public void recommendPlace(String fbUserId) {
		// recommend a place with the strategy of the given ranker
		List<RankedPlace> rankedPlaces = this.ranker.rankPlaces(fbUserId);
		for (RankedPlace rankedPlace : rankedPlaces) {
			logger.info("Place Name: " + rankedPlace.getName() + ", uri: " + rankedPlace.getUri() + ", score: " + rankedPlace.getScore() );
		}
	}

	private List<PolarPlace> disambiguatePlaces(Collection<PolarPlace> places) {
		List<PolarPlace> placesAfterDisambiguation = new ArrayList<PolarPlace>();
		for (PolarPlace place : places) {
			if(place != null){
			String placeNameBeforeDisambiguation = place.getName();
			PolarPlace placeAfterDisambiguation = this.disambiguator
					.disambiguatePlace(place);
			placesAfterDisambiguation.add(placeAfterDisambiguation);
			logger.info("Place name before: "
					+ placeNameBeforeDisambiguation
					+ ", place name after disambiguation: "
					+ placeAfterDisambiguation.getName()
					+ ", uri: "
					+ (placeAfterDisambiguation.getUri() != null ? placeAfterDisambiguation
							.getUri() : ""));
		}
		}
		return placesAfterDisambiguation;
	}

	private void storePlaces(String fbUserId, List<PolarPlace> placesToStore) {
		this.cypherRepository.startDB();
		for (PolarPlace place : placesToStore) {
			this.cypherRepository.insertPlaceNode(place);
			this.cypherRepository.mergeRelationShipBetweenNodes(fbUserId,
					"VISITED", place.getId());
			this.cypherRepository.mergePersonLikesPlaceRelationiship(place);
		}
		this.cypherRepository.stopDB();
	}

}
