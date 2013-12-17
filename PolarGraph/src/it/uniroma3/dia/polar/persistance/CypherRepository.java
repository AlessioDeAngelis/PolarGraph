package it.uniroma3.dia.polar.persistance;

import it.uniroma3.dia.polar.graph.model.Category;
import it.uniroma3.dia.polar.graph.model.Location;
import it.uniroma3.dia.polar.graph.model.Person;
import it.uniroma3.dia.polar.graph.model.PolarPlace;
import it.uniroma3.dia.polar.graph.model.RelTypes;
import it.uniroma3.dia.polar.utils.StringEscaper;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.ReadableIndex;

public class CypherRepository extends Repository {
	private GraphDatabaseService graphDb;
	private ExecutionEngine engine;

	public CypherRepository(String dbPath) {
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
			for (String s : columns) {
				System.out.println(s);
			}
			System.out.println("Rows " + rows);
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
				System.out.println(query);
				for (Category category : categories) {
					// Persist the category
					String categoryId = StringEscaper.convert(category.getId());
					String categoryName = StringEscaper.convert(category.getName());
					String partialQuery = "MERGE (category" + categoryId + ": Category{id:'" + categoryId + "',name:'"
							+ categoryName + "'}) \n";
					// Persist the relationship between the place and its
					// categories
//					query += partialQuery;
					this.engine.execute(partialQuery);
					System.out.println(partialQuery);
//					partialQuery += "match (place),(category) where place.id='" + placeId + "' AND category.id='"+categoryId +"' with (place) , (category) MERGE (place)-[:HAS_CATEGORY]->(category) \n";
					this.engine.execute(partialQuery);
					System.out.println(partialQuery);
					
				}
				Location location = place.getLocation();
				if (location != null) {

					// Persist the location
					String locationStreet = StringEscaper.convert(location.getStreet());
					String locationCity = StringEscaper.convert(location.getCity());
					String locationCountry = StringEscaper.convert(location.getCountry());
					double latitude = location.getLatitude();
					double longitude = location.getLongitude();
					String partialQuery = "match (place:Place) where place.id='" + placeId + "' with place MERGE (location: Location {street: '" + locationStreet + "', city: '"
							+ locationCity + "', country: '" + locationCountry + "', latitude:' " + latitude
							+ "',longitude:'" + longitude + "'}) MERGE (place)-[:LOCATED_IN]->(location) \n";
					// String partialQuery =
					// "MERGE (location: Location {city: '" +
					// locationCity
					// + "', country: '" + locationCountry + "', latitude:' " +
					// latitude + "',longitude:'" + longitude
					// + "'}) \n";
//					query += partialQuery;
					System.out.println(partialQuery);
					engine.execute(partialQuery);
//					query += partialQuery;
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
				String query = "MERGE (place: Place{id:'" + placeId + "',name:'" + placeName + "'}) \n";
				System.out.println(query);
				for (Category category : categories) {
					// Persist the category
					String categoryId = StringEscaper.convert(category.getId());
					String categoryName = StringEscaper.convert(category.getName());
					String partialQuery = "MERGE (category" + categoryId + ": Category{id:'" + categoryId + "',name:'"
							+ categoryName + "'}) \n";
					// Persist the relationship between the place and its
					// categories
					query += partialQuery;
					System.out.println(partialQuery);
					partialQuery = "MERGE (place)-[:HAS_CATEGORY]->(category" + categoryId + ") \n";
					query += partialQuery;
					System.out.println(partialQuery);
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
					System.out.println(partialQuery);
					partialQuery = "MERGE (place)-[:LOCATED_IN]->(location) \n";
					query += partialQuery;
					System.out.println(partialQuery);
				}
				this.engine.execute(query);

				tx.success();
			} finally {
				tx.close();
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
			System.out.println(query);
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

}
