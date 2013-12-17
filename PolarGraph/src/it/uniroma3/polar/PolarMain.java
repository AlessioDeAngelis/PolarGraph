package it.uniroma3.polar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import it.uniroma3.dia.polar.controller.PolarController;

public class PolarMain {
	public static void main(String[] args) {
		Properties props = loadProperties();
		String accessToken = props.getProperty("access_token");
		String dbPath = "C:\\Users\\Alessio\\Documents\\Neo4j\\polar.graphdb";
		dbPath = props.getProperty("db_path");
		
		String fbUserId = props.getProperty("fb_user_id");
		PolarController polarController = new PolarController(accessToken, dbPath);
		long start = System.currentTimeMillis();
		System.out.println("Start");
//		polarController.readUserFromFacebookAndStore(fbUserId);
		polarController.readVisitedPlacesFromFacebookAndStore(fbUserId);
//		polarController.readFriendsFromFacebookAndStore(fbUserId);
		
//		polarController.readPlacesVisitedByFriendsAndStore(fbUserId);
		polarController.readPlacesTaggedInPhotoAndStore(fbUserId);
//		polarController.readPlacesTaggedInPhotoByFriendsAndStore(fbUserId);

		System.out.println("End in " + (System.currentTimeMillis()-start) + " msec");
	}
	
	public static Properties loadProperties(){
		Properties prop = new Properties();
		try {
			FileInputStream fis = new FileInputStream("data/polar_graph.properties");
			prop.load(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}
}
