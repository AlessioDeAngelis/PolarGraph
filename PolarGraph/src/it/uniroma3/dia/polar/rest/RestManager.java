package it.uniroma3.dia.polar.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RestManager {
	
	public RestManager(){
		
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
