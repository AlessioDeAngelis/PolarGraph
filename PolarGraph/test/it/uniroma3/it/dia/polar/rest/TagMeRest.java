package it.uniroma3.it.dia.polar.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TagMeRest {
	public static void main(String[] args) {
		String text = "On this day 24 years ago Maradona scored his infamous 'Hand of God' goal against England in the quarter-final of the 1986";
		text = "Nigardsbreen";
		
		String parsedText = text.replace(" ", "+");
		String api = "bs6b86v37tb3ah6patt95fzc";
		try{
			String urlString = "http://tagme.di.unipi.it/tag?key=gaspare2014&text="+parsedText+"include_categories=true&tweet=true&include_all_spots=true" ;
		urlString = "http://tagme.di.unipi.it/tag?key=gaspare2014&include_categories=true&text="+parsedText;
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
