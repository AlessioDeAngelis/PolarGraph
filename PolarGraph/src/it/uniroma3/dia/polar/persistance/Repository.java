package it.uniroma3.dia.polar.persistance;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public abstract class Repository {

	private String dbPath;

	public Repository(String dbPath) {
		this.dbPath = dbPath;
	}

	public String getDbPath() {
		return this.dbPath;
	}

	public static void registerShutdownHook(final GraphDatabaseService graphDb) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running application).
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}

	/**
	 * Remove a node in the graph db
	 * */
	public void removeNode(Node node) {
		EmbeddedGraphDatabase graphDb = (EmbeddedGraphDatabase) new GraphDatabaseFactory()
				.newEmbeddedDatabase(this.getDbPath());
		Transaction tx = graphDb.beginTx();
		try {
			// we have first to remove all the relationships of the node
			for (Relationship relationship : node.getRelationships()) {
				relationship.delete();
			}
			// now we can delete the node
			node.delete();
			tx.success();
		} finally {
			tx.close();
		}

		registerShutdownHook(graphDb);
		graphDb.shutdown();
	}

	public abstract void insert(Object o, Index index);

	public abstract void remove(Object o);

	public void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}

}
