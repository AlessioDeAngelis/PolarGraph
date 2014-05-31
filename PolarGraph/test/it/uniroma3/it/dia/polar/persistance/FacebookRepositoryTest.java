package it.uniroma3.it.dia.polar.persistance;

import it.uniroma3.dia.polar.persistance.FacebookRepository;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

public class FacebookRepositoryTest {

	private Properties props;
	private static String accessToken;
	private static String dbPath;
	private static String facebookUserId;
	private FacebookRepository repository;

	@Before
	public void setup() {
		this.props = loadProperties();
		this.accessToken = props.getProperty("access_token");
		this.dbPath = props.getProperty("db_path");
		this.facebookUserId = props.getProperty("fb_user_id");
		this.repository = new FacebookRepository(accessToken);
	}

	public static Properties loadProperties() {
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

//	@Test
	public void retrievePlaceTest() {
		long start = System.currentTimeMillis();
		this.repository.retrieveVisitedPlacesPhotoTaggedByUserId(facebookUserId);
		long end = System.currentTimeMillis();
		System.out.println("ENDED in " + (end - start) +" (msec)");
	}
	
	@Test
	public void retrievePostsTest(){
		List<String> ss = this.repository.retrievePlacesByUserIdV2(facebookUserId, "/tagged_places");
		for(String s:ss){
			System.out.println(s);
		}
	}
}
