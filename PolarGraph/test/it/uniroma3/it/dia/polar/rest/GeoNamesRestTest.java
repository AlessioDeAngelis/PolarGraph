package it.uniroma3.it.dia.polar.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

public class GeoNamesRestTest {
	public static void main(String[] args) {
		String text = "Ciao a tutti oggi sono stato a Rome e ho girato il Colosseo con i miei amici";

		String parsedText = text.replace(" ", "+");
		String api = "bs6b86v37tb3ah6patt95fzc";
//		String urlString = "http://api.geonames.org/findNearbyWikipedia?lat=63.412691177065&lng=10.43272078882&username=alessio ";/moholt

//		String		urlString = "http://api.geonames.org/search?q=moholt&username=alessio&type=RDF";//risultato come rdf
//		String		urlString = "http://api.geonames.org/search?q=moholt+stud.by&username=alessio";//risultato come rdf
//				urlString = "http://api.geonames.org/search?q=Colosseum+stud.by&username=alessio&type=RDF";//risultato come rdf
				String urlString = "http://api.geonames.org/findNearbyWikipedia?lat=41.892167&lng=12.4943271&username=alessio "; //colosseo
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
