package it.uniroma3.dia.polar.persistance;

import it.uniroma3.dia.polar.graph.model.Person;
import it.uniroma3.dia.polar.graph.model.RelTypes;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.DefaultFileSystemAbstraction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.kernel.StoreLocker;
import org.neo4j.kernel.Traversal;

@Deprecated
/**
 * Use Cypher repository instead
 * */
public class PersonNeoRepository extends Repository {
private 		EmbeddedGraphDatabase graphDb;
	public PersonNeoRepository(String dbPath) {
		super(dbPath);
//		new GraphDatabaseFactory().newEmbeddedDatabase(dbPath).shutdown();
//        createLock(dbPath);
//        Map<String, String> config = new HashMap<String, String>();
//        config.put( "neostore.nodestore.db.mapped_memory", "10M" );
//        config.put( "string_block_size", "60" );
//        config.put( "array_block_size", "300" );
//         graphDb = (EmbeddedGraphDatabase) new GraphDatabaseFactory()
//            .newEmbeddedDatabaseBuilder(	dbPath)
//            .setConfig( config )
//            .newGraphDatabase();
//        Configurator configurator = new ServerConfigurator((GraphDatabaseAPI)graphDb);
//
//        configurator.configuration().setProperty(Configurator.WEBSERVER_ADDRESS_PROPERTY_KEY, "127.0.0.1");
//        configurator.configuration().setProperty(Configurator.WEBSERVER_PORT_PROPERTY_KEY, 7474);
//
//        WrappingNeoServerBootstrapper bootstrapper = new WrappingNeoServerBootstrapper((GraphDatabaseAPI)graphDb, configurator);
//        bootstrapper.start();
	}
	
	private void createLock(String dbPath){
		StoreLocker lock = new StoreLocker(new DefaultFileSystemAbstraction());
        lock.checkLock(new File(dbPath));
        try {
			lock.release();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			new File(dbPath, "store_lock").createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Insert a single object in the db
	 * */
	@Override
	public void insert(Object o, Index index) {
		Person person = (Person) o;
		EmbeddedGraphDatabase graphDb = (EmbeddedGraphDatabase) new GraphDatabaseFactory().newEmbeddedDatabase(this
				.getDbPath());
		Node node = null;
		Transaction tx = graphDb.beginTx();
		try {
			// add the person as a node in the graph
			node = graphDb.createNode();
			node.setProperty("id", person.getId());
			node.setProperty("name", person.getName());
			node.setProperty("surname", person.getSurname());
			// index the node for a fast retrieve by id
			IndexManager indexFromGraph = graphDb.index();
			Index<Node> persons = ((IndexManager) indexFromGraph).forNodes("persons");
			persons.add(node, "id", node.getId());
			System.out.println("STORED IN NEO4J " + node.getId()+" , " + node.getProperty("name"));

			tx.success();
		} finally {
			tx.finish();
		}

		registerShutdownHook(graphDb);
		graphDb.shutdown();
	}

	/**
	 * Insert a person in the db together with all her friends
	 * */
	public void insertPersonAndFriends(Person person) {
		EmbeddedGraphDatabase graphDb = (EmbeddedGraphDatabase) new GraphDatabaseFactory().newEmbeddedDatabase(this
				.getDbPath());
		Node node = null;
		Transaction tx = graphDb.beginTx();
		try {
			// add the person as a node in the graph
			node = graphDb.createNode();
			node.setProperty("id", person.getId());
			node.setProperty("name", person.getName());
			node.setProperty("surname", person.getSurname());
			if (person.getFriends() != null) {
				System.out.println(person.getFriends().size());
				for (Person friend : person.getFriends()) {
					Node nodeFriend = graphDb.createNode();
					nodeFriend.setProperty("id", friend.getId());
					nodeFriend.setProperty("name", friend.getName());
					nodeFriend.setProperty("surname", friend.getSurname());
					Relationship relationship = node.createRelationshipTo(nodeFriend, RelTypes.KNOWS);
//					nodeFriend.createRelationshipTo(node, RelTypes.KNOWS);
					System.out.println("STORED FRIEND IN NEO4J " + nodeFriend.getId()+" , " + nodeFriend.getProperty("name"));
				}
			}
			tx.success();
		} finally {
			tx.finish();
		}

		registerShutdownHook(graphDb);
		graphDb.shutdown();
	}

	@Override
	public void remove(Object o) {

	}

	/**
	 * Create a relationship "REVIEWS" between two nodes in the graph
	 * */
	public void addVisitToPerson(Node person, Node visited) {
		EmbeddedGraphDatabase graphDb = (EmbeddedGraphDatabase) new GraphDatabaseFactory().newEmbeddedDatabase(this
				.getDbPath());
		Transaction tx = graphDb.beginTx();
		try {
			if (person != null && visited != null) {
				person.createRelationshipTo(visited, RelTypes.REVIEWS);
			}
		} finally {
			tx.success();
		}
		registerShutdownHook(graphDb);
		graphDb.shutdown();
	}

	/**
	 * Create a relationship "KNOWS" between two nodes in the graph
	 * */
	public void addFriendToNode(Node node, Node nodeFriend) {
		EmbeddedGraphDatabase graphDb = (EmbeddedGraphDatabase) new GraphDatabaseFactory().newEmbeddedDatabase(this
				.getDbPath());
		Transaction tx = graphDb.beginTx();
		try {
			if (node != null && nodeFriend != null) {
				node.createRelationshipTo(nodeFriend, RelTypes.KNOWS);
				nodeFriend.createRelationshipTo(node, RelTypes.KNOWS);
			}
		} finally {
			tx.success();
		}
		registerShutdownHook(graphDb);
		graphDb.shutdown();
	}

	public void addFriendshipBetweenPersons(String personId, List<Person> friends) {
		EmbeddedGraphDatabase graphDb = (EmbeddedGraphDatabase) new GraphDatabaseFactory().newEmbeddedDatabase(this
				.getDbPath());
		Transaction tx = graphDb.beginTx();
		try {
			IndexManager indexFromGraph = graphDb.index();
			Index<Node> persons = ((IndexManager) indexFromGraph).forNodes("persons");
			Node node = (Node) persons.get("id", personId);
			for (Person friend : friends) {
				Node nodeFriend = (Node) persons.get("id", friend.getId());
				if (node != null && nodeFriend != null) {
					node.createRelationshipTo(nodeFriend, RelTypes.KNOWS);
					nodeFriend.createRelationshipTo(node, RelTypes.KNOWS);
				}
				System.out.println("Friend stored,name = " + friend.getName());// TODO:
																				// use
																				// logger
			}
		} finally {
			tx.success();
		}
		registerShutdownHook(graphDb);
	}

	public Person findIndexedPersonById(String id) {
		Person person = null;
		EmbeddedGraphDatabase graphDb = (EmbeddedGraphDatabase) new GraphDatabaseFactory().newEmbeddedDatabase(this
				.getDbPath());
		Transaction tx = graphDb.beginTx();
		try {
			IndexManager index = graphDb.index();
			Index<Node> persons = index.forNodes("persons");
			IndexHits<Node> hits = persons.get("id", id);
			Node node = hits.getSingle();
			if (node != null) {
				person.setId((String) node.getProperty("id"));
				person.setName((String) node.getProperty("name"));
				person.setSurname((String) node.getProperty("surname"));
			} else {
				System.out.println("NODE NOT FOUND");
			}
		} finally {
			tx.finish();
		}

		registerShutdownHook(graphDb);
		graphDb.shutdown();
		return person;
	}

	public Person findPersonById(String id) {
		EmbeddedGraphDatabase graphDb = (EmbeddedGraphDatabase) new GraphDatabaseFactory().newEmbeddedDatabase(this
				.getDbPath());
		Node nodeToFind = null;
		Person person = null;
		Transaction tx = graphDb.beginTx();
		try {
			for (Node node : graphDb.getAllNodes()) {
				if (node.getProperty("id") != null && node.getProperty("id").equals(id)) {
					nodeToFind = node;
					break;
				}
			}

			if (nodeToFind != null) {
				person = new Person();
				person.setId((String) nodeToFind.getProperty("id"));
				person.setName((String) nodeToFind.getProperty("name"));
				person.setSurname((String) nodeToFind.getProperty("surname"));
			} else {
			}
		} finally {
			tx.finish();
		}

		registerShutdownHook(graphDb);
		graphDb.shutdown();

		return person;
	}

	public Node findNodeByPerson(Person person) {
		return null;
	}

	public Person findPersonByNode(Node node) {
		// TODO
		TraversalDescription traversalDescription = Traversal.description().breadthFirst().evaluator(Evaluators.all());
		traversalDescription.traverse(node);
		return null;
	}
}
