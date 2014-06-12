package it.uniroma3.dia.polar.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class RestManager {
	
	public RestManager(){
		
	}
	
	public String queryGeonamesByName(String name) {
		String urlString = "http://api.geonames.org/search?q=" + name + "&username=alessio&type=RDF";
		String requestMethod = "GET";
		String requestProperty = "application/xml";
		String output = this.restOperation(urlString, requestMethod, requestProperty);
		return output;
	}

	public String queryGeonamesByLatLng(String lat, String lng) {
		RestManager restManager = new RestManager();
		String urlString = "http://api.geonames.org/findNearbyWikipedia?lat=" + lat + "&lng=" + lng
				+ "&username=alessio&type=RDF";
		String requestMethod = "GET";
		String requestProperty = "application/xml";
		String output = this.restOperation(urlString, requestMethod, requestProperty);
		return output;
	}

	public String querySpotlightByName(String name) {
		RestManager restManager = new RestManager();
		String urlString = "http://spotlight.sztaki.hu:2222/rest/annotate/?text="
				+ name.replace(" ", "+").replace(".", "+");
		String requestMethod = "GET";
		String requestProperty = "application/xml";
		String output = this.restOperation(urlString, requestMethod, requestProperty);
		return output;
	}
	
	public String queryTagMe(String text, String language) {
		RestManager restManager = new RestManager();		
		String parsedText = "";
		try {
			parsedText = URLEncoder.encode(text,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String urlString = "http://tagme.di.unipi.it/tag?key=gaspare2014&include_categories=true&text="+parsedText+"&lang="+language;

		String requestMethod = "GET";
		String requestProperty = "application/xml";
		String output = this.restOperation(urlString, requestMethod, requestProperty);
		return output;
	}
	
	public String queryTagMeLongText(String text, String language) {
		RestManager restManager = new RestManager();		
		String parsedText = "";
		try {
			parsedText = URLEncoder.encode(text,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String urlString = "http://tagme.di.unipi.it/tag?key=gaspare2014&long_text=true&include_categories=true&lang="+language+"&text="+parsedText;

		String requestMethod = "GET";
		String requestProperty = "application/xml";
		String output = this.restOperation(urlString, requestMethod, requestProperty);
		return output;
	}

	/**
	 * This method is used to query a rest service and generates its output.
	 * @param urlString is the url of the service you want to query
	 * @param requestMethod can be GET or POST
	 * @param requestProperty can be "application/json" or "application/xml"
	 * */
	public String restOperation(String urlString, String requestMethod, String requestProperty) {
		String output = "";
		String parsedUrl = urlString.replace(" ", "+"); // you have to replace
														// whitespaces
														// with +
		try {
			URL url = new URL(parsedUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(requestMethod);
			conn.setRequestProperty("Accept", requestProperty);
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String nextLine = "";
			while ((nextLine = br.readLine()) != null) {
				output += (nextLine);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}
}
