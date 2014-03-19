package it.uniroma3.it.dia.polar.rest;

import it.uniroma3.dia.polar.rest.RestManager;
import it.uniroma3.polar.PolarMain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSuMoholt {

	private final static Logger logger = LoggerFactory.getLogger(TestSuMoholt.class);

	public static void main(String[] args) {
//		logger.info("TESTING THE ACCURACY OF GEONAMES RESULTS");
//		logger.info("QUERY MOHOLT");
//		logger.debug(queryGeonamesByName("moholt") + "\n");
//		logger.info("QUERY MOHOLT stud.by");
//		logger.info(queryGeonamesByName("moholt stud by" + "\n"));
//		logger.info("QUERY Moholt, Sor-Trondelag, Norway");
//		logger.info(queryGeonamesByName("Moholt, Sor-Trondelag, Norway") + "\n");
//		logger.info("QUERY Moholt Studentby, Trondheim");
//		logger.info(queryGeonamesByName("Moholt Studentby, Trondheim") + "\n");
//		logger.info("QUERY Moholt Trondheim!");
//		logger.info(queryGeonamesByName("Moholt Trondheim"));
		
		logger.info("TESTING THE ACCURACY OF SPOTLIGHT RESULTS");
		logger.info("QUERY MOHOLT");
		logger.debug(querySpotlightByName("moholt") + "\n");
		logger.info("QUERY MOHOLT stud.by");
		logger.info(querySpotlightByName("moholt stud by" + "\n"));
		logger.info("QUERY Moholt, Sor-Trondelag, Norway");
		logger.info(querySpotlightByName("Moholt, Sor-Trondelag, Norway") + "\n");
		logger.info("QUERY Moholt Studentby, Trondheim");
		logger.info(querySpotlightByName("Moholt Studentby, Trondheim") + "\n");
		logger.info("QUERY Moholt Trondheim!");
		logger.info(querySpotlightByName("Moholt Trondheim"));
		
//		logger.info("TESTING THE ACCURACY OF GEONAMES BY LAT & LNG RESULTS");
//		logger.info("QUERY MOHOLT stud.by");
//		logger.info(queryGeonamesByLatLng("63.4115387872","10.4303010494")+"\n");
//		logger.info(queryGeonamesByLatLng("63.4115125972","10.4300937262")+"\n");
//		logger.info(queryGeonamesByLatLng("63.4101749","10.4277983")+"\n");
//		logger.info("QUERY Moholt, Sor-Trondelag, Norway");
//		logger.info(queryGeonamesByLatLng("63.4","10.4333")+"\n");
//		logger.info(queryGeonamesByLatLng("63.412691177065","10.43272078882")+"\n");



	}

	public static String queryGeonamesByName(String name) {
		RestManager restManager = new RestManager();
		String urlString = "http://api.geonames.org/search?q=" + name + "&username=alessio&type=RDF";
		String requestMethod = "GET";
		String requestProperty = "application/json";
		String output = restManager.restOperation(urlString, requestMethod, requestProperty);
		return output;
	}

	public static String queryGeonamesByLatLng(String lat, String lng) {
		RestManager restManager = new RestManager();
		String urlString = "http://api.geonames.org/findNearbyWikipedia?lat=" + lat + "&lng=" + lng
				+ "&username=alessio&type=RDF";
		String requestMethod = "GET";
		String requestProperty = "application/json";
		String output = restManager.restOperation(urlString, requestMethod, requestProperty);
		return output;
	}
	
	public static String querySpotlightByName(String name){
		RestManager restManager = new RestManager();
		String urlString = "http://spotlight.sztaki.hu:2222/rest/annotate/?text=" + name.replace(" ", "+").replace(".", "+");
		String requestMethod = "GET";
		String requestProperty = "application/xml";
		String output = restManager.restOperation(urlString, requestMethod, requestProperty);
		return output;
	}
}
