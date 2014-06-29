package it.uniroma3.it.dia.cicero.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ServizioRestEsempio {
	public static void main(String[] args) {
		String text = "Ciao a tutti oggi sono stato a Rome e ho girato il Colosseo con i miei amici";
		
		String parsedText = text.replace(" ", "+");
		String api = "bs6b86v37tb3ah6patt95fzc";
		try{
		URL url = new URL("http://api.opencalais.com/enlighten/rest/?licenseID=" + api + "&content="
				+ parsedText + "&paramsXML=");
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
