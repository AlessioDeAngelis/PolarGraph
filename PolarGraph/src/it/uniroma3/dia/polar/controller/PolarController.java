package it.uniroma3.dia.polar.controller;

import it.uniroma3.dia.polar.graph.model.Person;
import it.uniroma3.dia.polar.graph.model.PolarPlace;
import it.uniroma3.dia.polar.persistance.CypherRepository;
import it.uniroma3.dia.polar.persistance.FacebookRepository;

import java.util.List;

import com.google.inject.Inject;

public class PolarController {
	// TODO: don't hardcode string parameters!!!
	private final FacebookRepository facebookRepository;
	private final CypherRepository cypherRepository;
	private final String ACCESS_TOKEN = "";
	private final String DB_PATH = "";
	private Person currentPerson;

	public PolarController() {
		this.facebookRepository = new FacebookRepository(ACCESS_TOKEN);
		this.cypherRepository = new CypherRepository(DB_PATH);
		this.currentPerson = new Person();
	}

	public PolarController(final String accessToken, final String dbPath) {
		this.facebookRepository = new FacebookRepository(accessToken);
		this.cypherRepository = new CypherRepository(dbPath);
		this.currentPerson = new Person();
	}
	
	@Inject
	public PolarController(final FacebookRepository facebookRepository, final CypherRepository cypherRepository, final String accessToken, final String dbPath) {
		this.facebookRepository = facebookRepository;
		this.cypherRepository = cypherRepository;
		this.currentPerson = new Person();
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
		person.setFriends(friends);
		this.currentPerson = person;
		this.cypherRepository.startDB();
		this.cypherRepository.insertPersonAndFriends(person);
		this.cypherRepository.stopDB();
	}

	public void readPlacesVisitedByFriendsAndStore(String currentFbUserId) {
		// read the facebook friend ids only if you didn't do it already
		List<String> friendsId = this.facebookRepository.retrieveFriendsId(currentFbUserId);

		for (int i = 50; i < 60; i++) {
			String friendId = friendsId.get(i);
			this.readVisitedPlacesFromFacebookAndStore(friendId);
		}
	}

	public void readPlacesTaggedInPhotoByFriendsAndStore(String currentFbUserId) {
		// read the facebook friend ids only if you didn't do it already
		List<String> friendsId = this.facebookRepository.retrieveFriendsId(currentFbUserId);

		for (int i = 10; i < 20; i++) {
			String friendId = friendsId.get(i);
			this.readPlacesTaggedInPhotoAndStore(friendId);
		}
	}

	public void readPlacesTaggedInPhotoAndStore(String fbUserId) {
		List<PolarPlace> visitedPlaces = this.facebookRepository.retrieveVisitedPlacesPhotoTaggedByUserId(fbUserId);
		this.cypherRepository.startDB();
		for (PolarPlace place : visitedPlaces) {
			this.cypherRepository.insertPlaceNode(place);
			this.cypherRepository.mergeRelationShipBetweenNodes(fbUserId, "VISITED", place.getId());
			this.cypherRepository.mergePersonLikesPlaceRelationiship(place);
		}
		this.cypherRepository.stopDB();
	}

	public void readVisitedPlacesFromFacebookAndStore(String fbUserId) {
		List<PolarPlace> visitedPlaces = this.facebookRepository.retrieveVisitedPlacesByUserId(fbUserId, "/feed");
		this.cypherRepository.startDB();
		for (PolarPlace place : visitedPlaces) {
			this.cypherRepository.insertPlaceNode(place);
			this.cypherRepository.mergeRelationShipBetweenNodes(fbUserId, "VISITED", place.getId());
			this.cypherRepository.mergePersonLikesPlaceRelationiship(place);

		}
		this.cypherRepository.stopDB();
	}

}
