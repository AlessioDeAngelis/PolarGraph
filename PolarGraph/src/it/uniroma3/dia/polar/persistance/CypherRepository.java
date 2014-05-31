package it.uniroma3.dia.polar.persistance;

import it.uniroma3.dia.polar.graph.model.Category;
import it.uniroma3.dia.polar.graph.model.Couple;
import it.uniroma3.dia.polar.graph.model.Location;
import it.uniroma3.dia.polar.graph.model.Person;
import it.uniroma3.dia.polar.graph.model.PolarPlace;
import it.uniroma3.dia.polar.graph.model.RecommendedObject;
import it.uniroma3.dia.polar.utils.StringEscaper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

//import javax.servlet.ServletContext; //uncomment it
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;

public class CypherRepository extends Repository {

	private final Logger logger = LoggerFactory.getLogger(CypherRepository.class);

	private GraphDatabaseService graphDb;
	private ExecutionEngine engine;

	/**
	 * Named is for the injection of parameters from a properties file. Look at
	 * guice documentation for more info
	 * */
	@Inject
	public CypherRepository(@Named("db_path") String dbPath) {
		super(dbPath);
	}

	public void startDB() {
		// load database
		this.graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(this.getDbPath());
		graphDb.index().getNodeAutoIndexer().setEnabled(true);

		registerShutdownHook(graphDb);
		// Execution engin used for cypher queries
		this.engine = new ExecutionEngine(graphDb);
	}

	public void stopDB() {
		if (this.graphDb != null) {
			this.graphDb.shutdown();
		}
	}

	/**
	 * Insert a single object in the db
	 * */
	@Override
	public void insert(Object o, Index index) {

	}

	/**
	 * Insert a person in the db together with all her friends
	 * */
	public void insertPersonAndFriends(Person person) {

		Transaction tx = graphDb.beginTx();
		try {
			String id = person.getId();
			String name = person.getName().replaceAll("'", "_");
			String surname = person.getSurname().replaceAll("'", "_");
			/*
			 * Remember that Merge doesn't support map parameters but only
			 * parameters via rest
			 */
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("MERGE (me:Person {id:'" + id + "',name:'" + name + " ',surname:'" + surname + "'})");
			stringBuilder.append("\n");

			if (person.getFriends() != null) {
				for (Person friend : person.getFriends()) {
					// first create friend nodes if they don't exist
					String friendId = friend.getId();
					String friendName = friend.getName().replaceAll("'", "_");
					;
					String friendSurname = friend.getSurname().replaceAll("'", "_");
					;
					stringBuilder.append("MERGE (friend" + friendId + ":Person {id:'" + friendId + "',name:'"
							+ friendName + " ',surname:'" + friendSurname + "'})");
					stringBuilder.append("\n");
					// then merge the relationship
					stringBuilder.append("MERGE (me)-[:KNOWS]->(friend" + friendId + ")");
					stringBuilder.append("\n");
				}

			}
			String query = stringBuilder.toString();
			this.engine.execute(query);

			tx.success();
		} finally {
			tx.close();
		}
	}

	@Override
	public void remove(Object o) {

	}

	/**
	 * Removes all the nodes and relationship in the database
	 * */
	public void clearGraph() {
		Transaction tx = this.graphDb.beginTx();
		try {
			String query = "start n= node(*) MATCH (n)-[r]-() DELETE n, r";
			this.engine.execute(query);
			tx.success();
		} finally {
			tx.close();
		}
	}

	public Person findPersonByIdCypher(int id) {
		// EmbeddedGraphDatabase graphDb = (EmbeddedGraphDatabase) new
		// GraphDatabaseFactory().newEmbeddedDatabase(this
		// .getDbPath());
		Node nodeToFind = null;
		Person person = null;
		Transaction tx = graphDb.beginTx();
		ExecutionResult result;
		this.engine = new ExecutionEngine(graphDb);

		try {
			String query = "start n=node(*) where n.name='Alessio De Angelis' return n.name,n.surname,n.id";
			query = "start n=node(*) return n.name,n.id";
			// query =
			// "start n=node(*) where n.name='Alessio' return n.name,n.surname,n.id,n.type";

			result = engine.execute(query);
			List<String> columns = result.columns();
			String rows = "";
			for (Map<String, Object> row : result) {
				for (Entry<String, Object> column : row.entrySet()) {
					rows += column.getKey() + ": " + column.getValue() + "; ";
				}
				rows += "\n";
			}

			// for (String s : columns) {
			// System.out.println(s);
			// }
			// System.out.println("Rows " + rows);
			tx.success();
		} finally {
			tx.close();
		}

		// registerShutdownHook(graphDb);
		// graphDb.shutdown();

		return person;
	}

	public void insertPersonNode(Person person) {

		Transaction tx = graphDb.beginTx();
		try {
			String id = StringEscaper.convert(person.getId());
			String name = StringEscaper.convert(person.getName());
			String surname = StringEscaper.convert(person.getSurname());
			/* Merge doesn't support map parameters but only parameters via rest */
			String query = "MERGE (node:Person {id:'" + id + "',name:'" + name + " ',surname:'" + surname + "'})";
			this.engine.execute(query);
			tx.success();
		} finally {
			tx.close();
		}

	}

	@Deprecated
	public void insertPlaceNode4(PolarPlace place) {
		if (place != null) {
			Transaction tx = graphDb.beginTx();
			try {
				// the place is composed by its location and its categories
				// Let's start from the the place
				List<Category> categories = place.getCategories();
				String placeId = StringEscaper.convert(place.getId());
				String placeName = StringEscaper.convert(place.getName());
				String query = "MERGE (place: Place{id:'" + placeId + "',name:'" + placeName + "'}) ";
				this.engine.execute(query);
				logger.debug(query);
				for (Category category : categories) {
					// Persist the category
					String categoryId = StringEscaper.convert(category.getId());
					String categoryName = StringEscaper.convert(category.getName());
					String partialQuery = "MERGE (category" + categoryId + ": Category{id:'" + categoryId + "',name:'"
							+ categoryName + "'}) \n";
					// Persist the relationship between the place and its
					// categories
					// query += partialQuery;
					this.engine.execute(partialQuery);
					logger.debug(partialQuery);
					// partialQuery +=
					// "match (place),(category) where place.id='" + placeId +
					// "' AND category.id='"+categoryId
					// +"' with (place) , (category) MERGE (place)-[:HAS_CATEGORY]->(category) \n";
					this.engine.execute(partialQuery);
					logger.debug(partialQuery);

				}
				Location location = place.getLocation();
				if (location != null) {

					// Persist the location
					String locationStreet = StringEscaper.convert(location.getStreet());
					String locationCity = StringEscaper.convert(location.getCity());
					String locationCountry = StringEscaper.convert(location.getCountry());
					double latitude = location.getLatitude();
					double longitude = location.getLongitude();
					String partialQuery = "match (place:Place) where place.id='" + placeId
							+ "' with place MERGE (location: Location {street: '" + locationStreet + "', city: '"
							+ locationCity + "', country: '" + locationCountry + "', latitude:' " + latitude
							+ "',longitude:'" + longitude + "'}) MERGE (place)-[:LOCATED_IN]->(location) \n";
					// String partialQuery =
					// "MERGE (location: Location {city: '" +
					// locationCity
					// + "', country: '" + locationCountry + "', latitude:' " +
					// latitude + "',longitude:'" + longitude
					// + "'}) \n";
					// query += partialQuery;
					logger.debug(partialQuery);
					engine.execute(partialQuery);
					// query += partialQuery;
				}
				this.engine.execute(query);

				tx.success();
			} finally {
				tx.close();
			}
		}

	}

	public void insertPlaceNode(PolarPlace place) {
		if (place != null) {
			Transaction tx = graphDb.beginTx();
			try {
				// the place is composed by its location and its categories
				// Let's start from the the place
				List<Category> categories = place.getCategories();
				String placeId = StringEscaper.convert(place.getId());
				String placeName = StringEscaper.convert(place.getName());
				String uri = StringEscaper.convert(place.getUri());
				String query = "MERGE (place: Place{id:'" + placeId + "',name:'" + placeName + "', uri:'" + uri
						+ "'}) \n";
				logger.debug(query);
				for (Category category : categories) {
					// Persist the category
					String categoryId = StringEscaper.convert(category.getId());
					String categoryName = StringEscaper.convert(category.getName());
					String partialQuery = "MERGE (category" + categoryId + ": Category{id:'" + categoryId + "',name:'"
							+ categoryName + "'}) \n";
					// Persist the relationship between the place and its
					// categories
					query += partialQuery;
					logger.debug(partialQuery);
					partialQuery = "MERGE (place)-[:HAS_CATEGORY]->(category" + categoryId + ") \n";
					query += partialQuery;
					logger.debug(partialQuery);
				}
				Location location = place.getLocation();
				if (location != null) {

					// Persist the location
					String locationStreet = StringEscaper.convert(location.getStreet());
					String locationCity = StringEscaper.convert(location.getCity());
					String locationCountry = StringEscaper.convert(location.getCountry());
					double latitude = location.getLatitude();
					double longitude = location.getLongitude();
					String partialQuery = "MERGE (location: Location {street: '" + locationStreet + "', city: '"
							+ locationCity + "', country: '" + locationCountry + "', latitude:' " + latitude
							+ "',longitude:'" + longitude + "'}) \n";
					// String partialQuery =
					// "MERGE (location: Location {city: '" +
					// locationCity
					// + "', country: '" + locationCountry + "', latitude:' " +
					// latitude + "',longitude:'" + longitude
					// + "'}) \n";
					query += partialQuery;
					logger.debug(partialQuery);
					partialQuery = "MERGE (place)-[:LOCATED_IN]->(location) \n";
					query += partialQuery;
					logger.debug(partialQuery);
				}

				this.engine.execute(query);

				tx.success();
			} finally {
				tx.close();
			}
		}

	}

	/***
	 * According to the likedBy attribute it will create nodes and add likes
	 * relationship
	 */
	public void mergePersonLikesPlaceRelationiship(PolarPlace place) {
		if (place.getLikedBy() != null && place.getLikedBy().size() > 0) {
			Transaction tx = graphDb.beginTx();
			try {
				for (String personId : place.getLikedBy()) {
					String query = "MERGE (:Person{id:'" + personId + "'})";
					logger.debug(query);
					this.engine.execute(query);
				}
				tx.success();
			} finally {
				tx.close();
			}
			for (String personId : place.getLikedBy()) {
				mergeRelationShipBetweenNodes(personId, "LIKES", place.getId());
			}
		}

	}
	
	/**
	 * Create merging a relationship between a subject node and an object node,
	 * if it doesn't already exist You need to have the ids of the two nodes
	 * */
	public void mergeRelationShipBetweenNodes(String subjectId, String relationship, String objectId) {
		Transaction tx = graphDb.beginTx();
		try {

			String query = "MATCH (s),(o) WHERE s.id='" + subjectId + "' AND o.id='" + objectId
					+ "' WITH s,o MERGE (s)-[:" + relationship + "]->(o);";
			logger.debug(query);
			this.engine.execute(query);
			tx.success();
		} finally {
			tx.close();
		}
	}

	public void mergeRelationship(Person person) {

		Transaction tx = graphDb.beginTx();
		try {
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("id", person.getId());
			attributes.put("name", person.getName());
			attributes.put("surname", person.getSurname());
			attributes.put("type", "person");

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("prop", attributes);

			String query = "MERGE (p:Person{name:'domenico'})-[r:KNOWS]->(f:friend{name:'amico'});";

			this.engine.execute(query, params);
			tx.success();
		} finally {
			tx.close();
		}

	}

	/**
	 * This method finds all the nodes of the given type
	 * **/
	public void printAllNodesByType(String nodeType) {
		Transaction tx = graphDb.beginTx();
		try {

			String query = "MATCH (n:" + nodeType + ") return distinct n";
			logger.debug(query);
			ExecutionResult result = this.engine.execute(query);
			logger.info(result.dumpToString());
			tx.success();
		} finally {
			tx.close();
		}
	}

	/**
	 * This query will return all the places that the person visited together
	 * with the number of friends that also went there
	 * */
	public void countFriendsThatVisitedSimilarPlaces(String personId) {
		Transaction tx = graphDb.beginTx();
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("personId", personId);
			String query = "MATCH (me)-[:VISITED]->(place), (friend)-[:VISITED]->(place) WHERE me.id = {personId} "
					+ " RETURN place, count(friend) as visitors ORDER BY  visitors DESC";
			logger.debug(query);
			ExecutionResult result = this.engine.execute(query, params);
			logger.info(result.dumpToString());
			tx.success();
		} finally {
			tx.close();
		}
	}

	/**
	 * @return a map where each element is a couple place, number of friends
	 *         that visited it. The key is the id of the place and the value is
	 *         the couple place-count of visitors
	 * */
	public Map<String, Couple<PolarPlace, Long>> findPlacesVisitedByTheUserAndCountFriends(String userId) {
		Transaction tx = graphDb.beginTx();
		// a map where each element is a couple place, number of friends that
		// visited it. The key is the id of the place and the value is the
		// couple place-count of visitors
		Map<String, Couple<PolarPlace, Long>> placesCount = new HashMap<String, Couple<PolarPlace, Long>>();
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("personId", userId);

			String query = "MATCH (me)-[:VISITED]->(place), (place)-[:HAS_CATEGORY]->(category) WHERE me.id = {personId} with place, category match (friend)-[:VISITED]->(place)  "
					+ " RETURN place, category, count(friend) as visitors ORDER BY  visitors DESC";
			logger.debug(query);
			ExecutionResult result = this.engine.execute(query, params);
			String rows = "";
			String prevNodeId = ""; // it is used to check if you are changing
									// the place node. In fact there are more
									// rows with the same place node but
									// different categories
			PolarPlace place = null;
			for (Map<String, Object> row : result) {
				Node nodePlace = (Node) row.get("place");
				String nodePlaceId = (String) nodePlace.getProperty("id", "");
				String nodePlaceName = (String) nodePlace.getProperty("name", "");
				String nodePlaceUri = (String) nodePlace.getProperty("uri", "");
				if (!prevNodeId.equals(nodePlaceId)) {// create a new place node
														// only if the id is
														// different, otherwise
														// the place is the same
					prevNodeId = nodePlaceId;// update the prev node id value
					place = new PolarPlace();
					place.setId(nodePlaceId);
					place.setUri(nodePlaceUri);
					place.setName(nodePlaceName);
				}
				// add the category. Note that there can be more than one
				// category for each place
				Node nodeCategory = (Node) row.get("category");
				String nodeCategoryId = (String) nodeCategory.getProperty("id");
				String nodeCategoryName = (String) nodeCategory.getProperty("name");
				if (place != null) {
					Category category = new Category(nodeCategoryName, nodeCategoryId);
					place.addCategory(category);
				}
				Long visitors = (Long) row.get("visitors");
				placesCount.put(nodePlaceId, new Couple<PolarPlace, Long>(place, visitors));
			}
			tx.success();
		} finally {
			tx.close();
		}
		return placesCount;
	}

	public void queryLike(String personId) {
		Transaction tx = graphDb.beginTx();
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("personId", personId);
			String query = "MATCH (me)-[r:LIKES]->(place) return me,r,place ";
			logger.debug(query);
			ExecutionResult result = this.engine.execute(query, params);
			logger.info(result.dumpToString());
			tx.success();
		} finally {
			tx.close();
		}
	}

	public void queryLocationAndCategories(String personId) {
		Transaction tx = graphDb.beginTx();
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("personId", personId);
			String query = "MATCH (n)-[r:HAS_CATEGORY]->(c) RETURN n,r,c ";
			logger.debug(query);
			ExecutionResult result = this.engine.execute(query, params);
			logger.info(result.dumpToString());
			tx.success();
		} finally {
			tx.close();
		}
	}

	/**
	 * @return list of couples: category, number of places linked to that
	 *         category
	 * **/
	public List<Couple<Category, Long>> findPlacesVisitedByTheUserAndCountCategories(String userId) {
		Transaction tx = graphDb.beginTx();

		List<Couple<Category, Long>> categoriesCount = new ArrayList<Couple<Category, Long>>();
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("personId", userId);

			String query = "MATCH (me)-[:VISITED]->(place), (place)-[HAS_CATEGORY]->(category:Category) WHERE me.id = {personId} with category, count(category) as user_places_of_category  "
					+ "RETURN distinct category, user_places_of_category ORDER BY  user_places_of_category DESC";
			logger.debug(query);
			ExecutionResult result = this.engine.execute(query, params);

			for (Map<String, Object> row : result) {
				Node nodeCategory = (Node) row.get("category");
				String nodeCategoryId = (String) nodeCategory.getProperty("id", "");
				String nodeCategoryName = (String) nodeCategory.getProperty("name", "");
				Category category = new Category(nodeCategoryName, nodeCategoryId);
				long placesOfCategoryCount = (Long) row.get("user_places_of_category");
				categoriesCount.add(new Couple<Category, Long>(category, placesOfCategoryCount));
			}
			logger.info(result.dumpToString());

			tx.success();
		} finally {
			tx.close();
		}
		return categoriesCount;
	}

	public List<Couple<Category, Long>> findPlacesByCategories(String userId,
			List<Couple<Category, Long>> categoriesCount) {
		Transaction tx = graphDb.beginTx();

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("personId", userId);
			params.put("categoryId", categoriesCount.get(5).getFirst().getId());

			String queryLikes = "MATCH (me)-[:LIKES]->(place), (person)-[:VISITED]->(place), (place)-[HAS_CATEGORY]->(category:Category) WHERE category.id = {categoryId} and me.id = {personId} "
					+ "RETURN distinct place, count(person) as visitors ORDER BY visitors DESC";
			String query = "MATCH (person)-[:VISITED]->(place), (place)-[HAS_CATEGORY]->(category:Category) WHERE category.id = {categoryId} "
					+ "RETURN distinct place, count(person) as visitors ORDER BY visitors DESC";
			logger.debug(query);
			ExecutionResult result = this.engine.execute(query, params);
			
//			logger.info(result.dumpToString());

			tx.success();
		} finally {
			tx.close();
		}
		return categoriesCount;
	}

	/**
	 * @param userId
	 *            the facebook id of the currentUser
	 * @param categoryName
	 *            the name of the category linked to the places you want to find
	 * @return list of Couples: places - number of visiting people
	 *  
	 * */
	@Deprecated
	public List<Couple<PolarPlace, Long>> findPlacesBySingleCategoryName(String userId, String categoryName) {
		List<Couple<PolarPlace, Long>> placesAndVisitors = new ArrayList<Couple<PolarPlace, Long>>();

		Transaction tx = graphDb.beginTx();

		try {
			RecommendedObject obj = new RecommendedObject();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("personId", userId);
			params.put("categoryName", categoryName);
			String query = "MATCH (person)-[:VISITED]->(place), (place)-[HAS_CATEGORY]->(category:Category) WHERE category.name = {categoryName} "
					+ "RETURN distinct place, count(person) as visitors ORDER BY visitors DESC";
			logger.debug(query);
			ExecutionResult result = this.engine.execute(query, params);
//			logger.info(result.dumpToString());
			PolarPlace place = null;
			for (Map<String, Object> row : result) {
				Node nodePlace = (Node) row.get("place");
				String nodePlaceId = (String) nodePlace.getProperty("id", "");
				String nodePlaceName = (String) nodePlace.getProperty("name", "");
				String nodePlaceUri = (String) nodePlace.getProperty("uri", "");
				Long visitors = (Long) row.get("visitors");
				place = new PolarPlace();
				place.setId(nodePlaceId);
				place.setUri(nodePlaceUri);
				place.setName(nodePlaceName);
				placesAndVisitors.add(new Couple<PolarPlace, Long>(place, visitors));
			}
			tx.success();
		} finally {
			tx.close();
		}
		return placesAndVisitors;
	}

	/**
	 * @param userId
	 *            the facebook id of the currentUser
	 * @param categoryNamesList
	 *            the list containing all the names of the categories linked to
	 *            the places you want to find
	 * @return list of Couples: places - number of visiting people
	 * 
	 * */
	public List<Couple<PolarPlace, Long>> findPlacesByMultiplesCategoryNames(String userId,
			List<String> categoryNamesList) {
		List<Couple<PolarPlace, Long>> placesAndVisitors = new ArrayList<Couple<PolarPlace, Long>>();
		ExecutionResult result = null;
		Transaction tx = graphDb.beginTx();
		Map<String, Object> params = null;
		String query = "";
		try {
			if (categoryNamesList != null && categoryNamesList.size() > 0) {
				int i = 0;

				 params = new HashMap<String, Object>();
				params.put("personId", userId);
				params.put("categoryName" + i, categoryNamesList.get(i));

				// constructing the query string
				query = "MATCH (person)-[:VISITED]->(place), (place)-[:HAS_CATEGORY]->(category:Category) WHERE category.name = {categoryName"
						+ i + "} ";
				for (i = 1; i < categoryNamesList.size(); i++) {
					params.put("categoryName" + i, categoryNamesList.get(i));
					query += " OR category.name = {categoryName" + i + "} ";
				}
				query += "RETURN distinct place, count(person) as visitors ORDER BY visitors DESC";
				logger.debug(query);

			}

			// querying the engine
			result = this.engine.execute(query, params);
//			logger.info(result.dumpToString());
			PolarPlace place = null;
			for (Map<String, Object> row : result) {
				Node nodePlace = (Node) row.get("place");
				String nodePlaceId = (String) nodePlace.getProperty("id", "");
				String nodePlaceName = (String) nodePlace.getProperty("name", "");
				String nodePlaceUri = (String) nodePlace.getProperty("uri", "");				
				Long visitors = (Long) row.get("visitors");
				place = new PolarPlace();
				place.setId(nodePlaceId);
				place.setUri(nodePlaceUri);
				place.setName(nodePlaceName);
				placesAndVisitors.add(new Couple<PolarPlace, Long>(place, visitors));
			}

			tx.success();
		} finally {
			tx.close();
		}
		return placesAndVisitors;
	}
	
	/**
	 * @param userId
	 *            the facebook id of the currentUser
	 * @param categoryNamesList
	 *            the list containing all the names of the categories linked to
	 *            the places you want to find
	 * @return list of Couples: places - number of visiting people
	 * 
	 * */
	public List<Couple<PolarPlace, Long>> findPlacesByMultiplesCategoryNamesCollaborativeFiltering(String userId,
			List<String> categoryNamesList) {
		List<Couple<PolarPlace, Long>> placesAndVisitors = new ArrayList<Couple<PolarPlace, Long>>();
		ExecutionResult result = null;
		Transaction tx = graphDb.beginTx();
		Map<String, Object> params = null;
		String query = "";
		try {
			if (categoryNamesList != null && categoryNamesList.size() > 0) {
				int i = 0;

				 params = new HashMap<String, Object>();
				params.put("personId", userId);
				params.put("categoryName" + i, categoryNamesList.get(i));

				// constructing the query string
				query = "MATCH (person:Person)-[:VISITED]->()<-[:VISITED]-(friend:Person)-[:VISITED]->(place), (place)-[:HAS_CATEGORY]->(category:Category) WHERE person.id = {personId} AND (category.name = {categoryName"
						+ i + "} ";
				for (i = 1; i < categoryNamesList.size(); i++) {
					params.put("categoryName" + i, categoryNamesList.get(i));
					query += " OR category.name = {categoryName" + i + "} ";
				}
				query += ") RETURN distinct place, count(person) as visitors ORDER BY visitors DESC";
				logger.debug(query);

			}

			// querying the engine
			result = this.engine.execute(query, params);
//			logger.info(result.dumpToString());
			PolarPlace place = null;
			for (Map<String, Object> row : result) {
				Node nodePlace = (Node) row.get("place");
				String nodePlaceId = (String) nodePlace.getProperty("id", "");
				String nodePlaceName = (String) nodePlace.getProperty("name", "");
				String nodePlaceUri = (String) nodePlace.getProperty("uri", "");				
				Long visitors = (Long) row.get("visitors");
				place = new PolarPlace();
				place.setId(nodePlaceId);
				place.setUri(nodePlaceUri);
				place.setName(nodePlaceName);
				placesAndVisitors.add(new Couple<PolarPlace, Long>(place, visitors));
			}

			tx.success();
		} finally {
			tx.close();
		}
		return placesAndVisitors;
	}
	
	/**
	 * Create a triple in the graph: personId -[TALKS_ABOUT]->concept
	 * */
	public void mergeUserTalksAboutRelationship(String subjectId, String objectName, String objectUri) {
		Transaction tx = graphDb.beginTx();
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			Map<String,Object> conceptProps =  new HashMap<>();
				params.put("personId", subjectId);
//				params.put("conceptProps", conceptProps);
				params.put("objectName", objectName);
				params.put("objectUri", objectUri);

//			String query = "MATCH (s) WHERE s.id={personId} WITH s MERGE (s)-[:TALKS_ABOUT]->(o:Concept{name: {conceptProps}.name, uri: {conceptProps}.uri});";
			String query = "MATCH (s) WHERE s.id={personId} WITH s MERGE (s)-[:TALKS_ABOUT]->(o:Concept{name: {objectName}, uri: {objectUri}});";

			logger.debug(query);
			this.engine.execute(query,params);
			tx.success();
		} finally {
			tx.close();
		}
	}

}
