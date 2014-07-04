package it.uniroma3.dia.cicero.persistance;

import it.uniroma3.dia.cicero.persistance.jdbc.DataSource;
import it.uniroma3.dia.cicero.persistance.jdbc.IDBroker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * SQL Repository for the evaluation
 * 
 * */
public class EvaluationRepository {

	public void storeUserRatingForRecommender(int userId, int recommenderId,
			int rating, String novelty, String serendipity) {
		DataSource dataSource = DataSource.getInstance();
		Connection connection = dataSource.getConnection();
		PreparedStatement statement = null;
		String insertQuery = "INSERT INTO Ratings(RatingID, UserID, Recommender, Rating, Novelty, Serendipity) VALUES(?,?,?,?,?,?)";
		try {
			statement = connection.prepareStatement(insertQuery);
			Long ratingId = IDBroker.getId(connection);
			statement.setLong(1, ratingId);
			statement.setInt(2, userId);
			statement.setInt(3, recommenderId);
			statement.setInt(4, rating);
			statement.setString(5, novelty);
			statement.setString(6, serendipity);

			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * CREATE TABLE Users ( UserID int, FacebookID varchar(255), Birthday
	 * varchar(255), Gender varchar(255), Location varchar(255), PRIMARY KEY
	 * (UserID) );
	 */
	public void storeUserInfo(String facebookId, String birthday,
			String gender, String location) {
		DataSource dataSource = DataSource.getInstance();
		Connection connection = dataSource.getConnection();
		PreparedStatement statement = null;
		String insertQuery = "INSERT INTO Users(UserID, FacebookID, Birthday, Gender, Location) VALUES(?,?,?,?,?)";
		try {
			statement = connection.prepareStatement(insertQuery);
			Long userId = IDBroker.getId(connection);
			statement.setLong(1, userId);
			statement.setString(2, facebookId);
			statement.setString(3, birthday);
			statement.setString(4, gender);
			statement.setString(5, location);

			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * CREATE TABLE Users ( UserID int, FacebookID varchar(255), Birthday
	 * varchar(255), Gender varchar(255), Location varchar(255), PRIMARY KEY
	 * (UserID) );
	 */
	public int findUserIdByFacebookID(String facebookId) {
		DataSource dataSource = DataSource.getInstance();
		Connection connection = dataSource.getConnection();
		PreparedStatement statement = null;
		String selectQuery = "SELECT UserID FROM Users WHERE FacebookID = ? ";
		int userId = 0;
		try {
			statement = connection.prepareStatement(selectQuery);
			statement.setString(1, facebookId);	
			
			ResultSet result = statement.executeQuery();
			if(result.next()){
				userId = result.getInt("UserID");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return userId;
	}
}
