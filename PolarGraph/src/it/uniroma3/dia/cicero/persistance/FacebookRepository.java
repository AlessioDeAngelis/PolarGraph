package it.uniroma3.dia.cicero.persistance;

import it.uniroma3.dia.cicero.graph.model.Category;
import it.uniroma3.dia.cicero.graph.model.FBPage;
import it.uniroma3.dia.cicero.graph.model.Location;
import it.uniroma3.dia.cicero.graph.model.Person;
import it.uniroma3.dia.cicero.graph.model.PolarPlace;
import it.uniroma3.dia.cicero.utils.ApplicationProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.Parameter;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.types.CategorizedFacebookType;
import com.restfb.types.NamedFacebookType;
import com.restfb.types.Photo;
import com.restfb.types.Post;
import com.restfb.types.Post.Likes;
import com.restfb.types.User;

public class FacebookRepository {
	private final Logger logger = LoggerFactory.getLogger(FacebookRepository.class);

	private String accessTokenString;
	private FacebookClient facebookClient;
	private AccessToken accessToken;

	@Inject
	public FacebookRepository(@Named("access_token") String accessTokenString) {
		this.accessTokenString = accessTokenString;
		this.facebookClient = new DefaultFacebookClient(accessTokenString);
	}

	public void setAccessTokenString(String accessTokenString) {
		this.accessTokenString = accessTokenString;
	}

	/**
	 * Call this method to extend the access token time_to_live to 60 days
	 * (instead of 1 hour)
	 * */
	public void extendTokenLife() {
		this.accessToken = this.facebookClient.obtainExtendedAccessToken(ApplicationProperties.FACEBOOK_CLIENT_ID,
				ApplicationProperties.FACEBOOK_SECURE_KEY, accessTokenString);
		this.accessTokenString = this.accessToken.getAccessToken();
		this.facebookClient = new DefaultFacebookClient(this.accessToken.getAccessToken());
	}

	public User retrieveLoggedUser() {
		return this.facebookClient.fetchObject("me", User.class); //me
	}

	public Person retrievePersonByUserId(String fbUserId) {
		Person person = new Person();
		User user = this.facebookClient.fetchObject(fbUserId, User.class);
		person = fbUserToPerson(user);
		logger.debug(person.getId() + " , " + person.getName() + " retrieved from facebook");
		return person;
	}

	private Person fbUserToPerson(User user) {
		Person person = new Person();
		person.setId(user.getId()); // TODO: later you should map id user with
									// facebook id
		person.setName(user.getFirstName());
		person.setSurname(user.getLastName());
		return person;
	}

	/**
	 * Returns only the ids of the friends, in order to speed up the query when
	 * you need only the ids and not all the personal info of the friends
	 * */
	public List<String> retrieveFriendsId(String currentFbUserId) {
		Connection<User> userFriendsConnection = facebookClient.fetchConnection(currentFbUserId + "/friends",
				User.class);
		List<String> facebookIds = new ArrayList<String>();
		for (List<User> userFriendsList : userFriendsConnection) {
			for (User user : userFriendsList) {
				// we take all the facebook ids of the users, since it is the
				// only info retrieved
				facebookIds.add(user.getId());
			}
		}
		return facebookIds;
	}

	public List<Person> retrieveFriendsByUserId(String fbUserId) {
		// TODO: something that we could do is retrieving only the ids of the
		// user and the name and merging the other personal info only if they
		// are really needed, for example only when the person is a user of
		// the system
		this.logger.info("RETRIEVING THE FACEBOOK FRIENDS OF THE USER " + fbUserId);
		List<Person> friends = new ArrayList<Person>();
		// we retrieve from facebook of the friends of the current user, given
		// its id
		Connection<User> userFriendsConnection = facebookClient.fetchConnection(fbUserId + "/friends", User.class);
		List<String> facebookIds = new ArrayList<String>();
		for (List<User> userFriendsList : userFriendsConnection) {
			for (User user : userFriendsList) {
				// we take all the facebook ids of the users, since it is the
				// only info retrieved
				facebookIds.add(user.getId());
			}
		}
		// for each id we query facebook in order to have further info of each
		// friend of the current user
		for (String friendFacebookId : facebookIds) {
			User facebookFriend = facebookClient.fetchObject(friendFacebookId, User.class);
			Person friend = fbUserToPerson(facebookFriend);
			friends.add(friend);
			logger.debug(friend.getId() + " , " + friend.getName() + " retrieved from facebook");
		}
		this.logger.info("END");

		return friends;
	}

	/**
	 * @param fbUserId
	 *            is the facebook id of the user that wants to be retrieved
	 * @param source
	 *            is the facebook path of the resources in the graph api , for
	 *            example /feed, /posts
	 * */
	public List<String> retrievePostsByUserId(String fbUserId, String source) throws NullPointerException {
		this.logger.info("RETRIEVING THE POSTS BY THE USER " + fbUserId);

		List<String> messages = new ArrayList<>();
		Connection<Post> userFeed = facebookClient.fetchConnection(fbUserId + source, Post.class);
		/*
		 * Firstly get all the feed post
		 */
		int numberOfConnections = 0;
		for (List<Post> myFeedConnectionPage : userFeed) {
			numberOfConnections++;
			// TODO: remove the number of connections
			// TODO: use a set to avoid duplication
			// if (numberOfConnections > 20) {
			// break;
			// }
			int numberOfPostToAnalyze = myFeedConnectionPage.size() - 1;
			for (int i = 0; i < numberOfPostToAnalyze; i++) {
				Post post = myFeedConnectionPage.get(i);
				/*
				 * We are interested only in the post that have some place
				 * bounded
				 */
				try {
					String message = post.getMessage();
					if (message != null && !message.equals(" ") && !message.equals("")) {
						messages.add(message);
					}
				} catch (NullPointerException e) {

				}
			}
		}
		this.logger.info("END");

		return messages;
	}

	/**
	 * @param fbUserId
	 *            is the facebook id of the user that wants to be retrieved
	 * @param source
	 *            is the facebook path of the resources in the graph api , for
	 *            example /feed, /posts
	 * */
	public List<String> retrievePlacesByUserIdV2(String fbUserId, String source) throws NullPointerException {
		this.logger.info("RETRIEVING THE POSTS BY THE USER " + fbUserId);

		List<String> messages = new ArrayList<>();

		// Here's how to fetch a connection

		JsonObject photosConnection = facebookClient.fetchObject("v2.0/me/tagged_places", JsonObject.class,
				Parameter.with("limit", 300));

		JsonArray array = photosConnection.getJsonArray("data");
		System.out.println(array);
		for (int i = 0; i < array.length(); i++) {
			JsonObject object = array.getJsonObject(i);
			JsonObject placeString = object.getJsonObject("place");
			// System.out.println(placeString);
			// TODO: vedere se questo � fattibile da fare con l'oath
		}

		return messages;
	}

	// TODO: find a common method for retrieving to avoid duplicate code
	/**
	 * @param fbUserId
	 *            is the facebook id of the user that wants to be retrieved
	 * @param source
	 *            is the facebook path of the resources in the graph api , for
	 *            example /feed, /posts
	 * */
	@Deprecated
	public List<PolarPlace> retrieveVisitedPlacesByUserId(String fbUserId, String source) throws NullPointerException {
		this.logger.info("RETRIEVING THE PLACES VISITED (POSTS) BY THE USER " + fbUserId);

		List<PolarPlace> visitedPlaces = new ArrayList<PolarPlace>();
		Connection<Post> userFeed = facebookClient.fetchConnection(fbUserId + source, Post.class);
		/*
		 * Firstly get all the feed post
		 */
		int numberOfConnections = 0;
		for (List<Post> myFeedConnectionPage : userFeed) {
			numberOfConnections++;
			// TODO: remove the number of connections
			// TODO: use a set to avoid duplication
			if (numberOfConnections > 20) {
				break;
			}
			int numberOfPostToAnalyze = myFeedConnectionPage.size() - 1;
			for (int i = 0; i < numberOfPostToAnalyze; i++) {
				Post post = myFeedConnectionPage.get(i);
				/*
				 * We are interested only in the post that have some place
				 * bounded
				 */
				try {
					PolarPlace visitedPlace = null;
					if (post != null && post.getPlace() != null && post.getPlace().getLocation() != null) {
						// Construct the polar place
						visitedPlace = new PolarPlace();
						String placeId = post.getPlace().getId();
						String placeName = post.getPlace().getName();
						String locationStreet = post.getPlace().getLocation().getStreet();
						String locationCity = post.getPlace().getLocation().getCity();
						String locationCountry = post.getPlace().getLocation().getCountry();
						double latitude = 0;
						double longitude = 0;
						if (post.getPlace().getLocation().getLatitude() != null
								&& post.getPlace().getLocation().getLongitude() != null) {
							latitude = post.getPlace().getLocation().getLatitude();
							longitude = post.getPlace().getLocation().getLongitude();
						}

						Location location = new Location(locationStreet, locationCity, locationCountry, latitude,
								longitude);

						visitedPlace.setId(placeId);
						visitedPlace.setName(placeName);
						visitedPlace.setLocation(location);

						// add the likes
						if (post.getLikes() != null) {
							Likes likes = post.getLikes();
							for (NamedFacebookType type : likes.getData()) {
								visitedPlace.addLikedBy(type.getId());
							}
						}

						// We fetch the page of the place from facebook because
						// we
						// need the categories
						FBPage mypage = facebookClient.fetchObject(placeId, FBPage.class);
						if (mypage != null) {
							long likesCount = 0;
							try {
								likesCount = mypage.getLikes();
							} catch (NullPointerException e) {

							}
							visitedPlace.setLikesCount(likesCount);
							for (CategorizedFacebookType fbCategory : mypage.getCategoryList()) {
								Category category = new Category();
								category.setId(fbCategory.getId());
								category.setName(fbCategory.getName());
								visitedPlace.addCategory(category);
							}
						}
					}
					visitedPlaces.add(visitedPlace);
					logger.debug(visitedPlace.toString());

				} catch (NullPointerException e) {

				}
			}
		}
		this.logger.info("END");

		return visitedPlaces;
	}

	@Deprecated
	public List<PolarPlace> retrieveVisitedPlacesPhotoTaggedByUserId(String fbUserId) throws NullPointerException {
		this.logger.info("RETRIEVING THE PLACES VISITED (PHOTOS) BY THE USER " + fbUserId);

		List<PolarPlace> visitedPlaces = new ArrayList<PolarPlace>();
		Connection<Photo> userFeed = facebookClient.fetchConnection(fbUserId + "/photos", Photo.class);
		/*
		 * Firstly get all the feed post
		 */
		int numberOfConnections = 0;
		for (List<Photo> myFeedConnectionPage : userFeed) {
			numberOfConnections++;
			// TODO: remove the number of connections
			// TODO: use a set to avoid duplication
			if (numberOfConnections > 20) {
				break;
			}
			int numberOfPostToAnalyze = myFeedConnectionPage.size() - 1;
			for (int i = 0; i < numberOfPostToAnalyze; i++) {
				Photo photo = myFeedConnectionPage.get(i);
				/*
				 * We are interested only in the post that have some place
				 * bounded
				 */

				try {
					if (photo != null && photo.getPlace() != null && photo.getPlace().getLocation() != null) {
						// Construct the polar place
						PolarPlace visitedPlace = new PolarPlace();
						String placeId = photo.getPlace().getId();
						String placeName = photo.getPlace().getName();
						String locationStreet = photo.getPlace().getLocation().getStreet();
						String locationCity = photo.getPlace().getLocation().getCity();
						String locationCountry = photo.getPlace().getLocation().getCountry();
						double latitude = 0;
						double longitude = 0;
						if (photo.getPlace().getLocation().getLatitude() != null
								&& photo.getPlace().getLocation().getLongitude() != null) {
							latitude = photo.getPlace().getLocation().getLatitude();
							longitude = photo.getPlace().getLocation().getLongitude();
						}
						Location location = new Location(locationStreet, locationCity, locationCountry, latitude,
								longitude);

						visitedPlace.setId(placeId);
						visitedPlace.setName(placeName);
						visitedPlace.setLocation(location);

						// add the likes
						if (photo.getLikes() != null) {
							List<NamedFacebookType> likes = photo.getLikes();
							for (NamedFacebookType type : likes) {
								visitedPlace.addLikedBy(type.getId());
							}
						}

						// We fetch the page of the place from facebook because
						// we
						// need the categories
						FBPage mypage = facebookClient.fetchObject(placeId, FBPage.class);
						if (mypage != null) {
							if (mypage.getLikes() != null) {
								long likesCount = mypage.getLikes();
								visitedPlace.setLikesCount(likesCount);
							}
							for (CategorizedFacebookType fbCategory : mypage.getCategoryList()) {
								Category category = new Category();
								category.setId(fbCategory.getId());
								category.setName(fbCategory.getName());
								visitedPlace.addCategory(category);
							}
						}
						visitedPlaces.add(visitedPlace);
						logger.debug(visitedPlace.toString());
						if (photo.getLikes() != null) {
							List<NamedFacebookType> likes = photo.getLikes();
							for (NamedFacebookType type : likes) {
								logger.debug(type.toString());
							}
						}
					}
				} catch (NullPointerException e) {

				}
			}
		}
		this.logger.info("END");

		return visitedPlaces;
	}

	public List<PolarPlace> retrieveUserTaggedLocations(String fbUserId) {
		JsonObject photosConnection = facebookClient.fetchObject("v1.0/"+fbUserId+"/locations", JsonObject.class,
				Parameter.with("limit", 300));

		JsonArray array = photosConnection.getJsonArray("data");
		List<PolarPlace> places = new ArrayList<>();
		try{
		for (int i = 0; i < array.length(); i++) {
			JsonObject object = array.getJsonObject(i);
			// System.out.println(object.toString());
			if (object.has("place")) {
				JsonObject placeObject = object.getJsonObject("place");
	
				PolarPlace visitedPlace = new PolarPlace();
				String placeId = placeObject.getString("id");
				String placeName = placeObject.getString("name");
				
				if(placeObject.has("location")){
					JsonObject locationObject = placeObject.getJsonObject("location");
					Location location = new Location();
					double latitude = locationObject.getDouble("latitude");
					double longitude = locationObject.getDouble("longitude");
					String city = "";
					String street = "";
					String country = "";
					if(locationObject.has("city")){
						city = locationObject.getString("city");
					}
					if(locationObject.has("street")){
						street = locationObject.getString("street");
					}if(locationObject.has("country")){
						country = locationObject.getString("country");
					}
					location.setCountry(country);
					location.setCity(city);
					location.setStreet(street);
					location.setLatitude(latitude);
					location.setLongitude(longitude);
					visitedPlace.setLocation(location);
				}
				visitedPlace.setId(placeId);
				visitedPlace.setName(placeName);
				
				//do not add the place if you already have it to limit the api calls
				boolean placeInTheList = false;
				for(PolarPlace pp : places){
					if(placeId.equals(pp.getId())){
						placeInTheList = true;
						break;
					}
				}
				
				if(!placeInTheList){
					// We fetch the page of the place from facebook because
					// we
					// need the categories
					FBPage mypage = facebookClient.fetchObject(placeId, FBPage.class);
					if (mypage != null) {
						if (mypage.getLikes() != null) {
							long likesCount = mypage.getLikes();
							visitedPlace.setLikesCount(likesCount);
						}
						for (CategorizedFacebookType fbCategory : mypage.getCategoryList()) {
							Category category = new Category();
							category.setId(fbCategory.getId());
							category.setName(fbCategory.getName());
							visitedPlace.addCategory(category);
						}
					}
					places.add(visitedPlace);
				}
				
			}
		}
		}catch(com.restfb.json.JsonException e){
			
		}
		return places;
	}
}
