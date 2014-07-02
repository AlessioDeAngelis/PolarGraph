package it.uniroma3.dia.cicero.persistance.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class IDBroker {

	private static Logger logger = Logger
			.getLogger("it.uniroma3.persistence.jdbc.IdBroker");
	private static boolean created = false;

	// valide per postgresql (anche queste stringhe potrebbero essere scritte
	// nel file di configurazione
	//private static final String ddlStatement = "CREATE SEQUENCE sequence_id";
	private static final String query = "SELECT nextval('sequence_id') AS id";;

	private IDBroker() {
	}

	/*private static void createSequence(Connection connection) {
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement(ddlStatement);
			if (statement.executeUpdate() == 1)
				created = true;
		} catch (SQLException e) {
			logger.severe("Errore SQL: " + e.getMessage());
			try {
				throw new PersistenceException(e.getMessage());
			} catch (PersistenceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}*/

	public static Long getId(Connection connection) {
		long id = -1;
		if (!created) {
			//createSequence(connection);
			created = true;
		}
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();
			result.next();
			id = result.getLong("id");
		} catch (SQLException e) {
			logger.severe("Errore SQL: " + e.getMessage());
			try {
				throw new Exception(e.getMessage());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return new Long(id);
	}
}
