package it.uniroma3.it.dia.polar.parser;

import it.uniroma3.dia.polar.disambiguator.SpottedPlace;
import it.uniroma3.dia.polar.parser.JSONParser;
import it.uniroma3.dia.polar.persistance.FacebookRepository;
import it.uniroma3.dia.polar.rest.RestManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

public class TagMeParsingTest {

	private Properties props;
	private static String accessToken;
	private static String dbPath;
	private static String facebookUserId;
	private FacebookRepository repository;
	private JSONParser jsonParser;
	private RestManager restManager;

	@Before
	public void setup() {
		this.props = loadProperties();
		this.accessToken = props.getProperty("access_token");
		this.dbPath = props.getProperty("db_path");
		this.facebookUserId = props.getProperty("fb_user_id");
		this.repository = new FacebookRepository(accessToken);
		this.jsonParser = new JSONParser();
		this.restManager = new RestManager();
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

	@Test
	public void parseTagMeTest() {
		List<String> messages = readPostsFile();

		for (String message : messages) {
			try {
				String encoded = URLEncoder.encode(message, "UTF-8");
				String parsedText = this.restManager.queryTagMeLongText(message.replace(".", "+"), "it");
				List<SpottedPlace> spots = this.jsonParser.parseTagMe(parsedText,0.5);
				for (SpottedPlace spot : spots) {
					System.out.println(message + "===========" + spot.getName() + " , " + spot.getUri());
				}
				parsedText = this.restManager.queryTagMeLongText(message.replace(".", "+"), "en");
				spots = this.jsonParser.parseTagMe(parsedText,0.5);
				for (SpottedPlace spot : spots) {
					System.out.println(message + "===========" + spot.getName() + " , " + spot.getUri());
				}
			} catch (Exception e) {
			}
		}
	}

	private List<String> readPostsFile() {
		List<String> posts = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader("data/useful_files/miei_facebook_posts"))) {

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				posts.add(sCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return posts;

	}
}
