package it.uniroma3.dia.polar.parser;

import it.uniroma3.dia.polar.disambiguator.SpottedPlace;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONParser {

	public List<SpottedPlace> parseTagMe(String jsonText,double threshold) {
		JSONObject obj = new JSONObject(jsonText);
		List<SpottedPlace> places = new ArrayList<SpottedPlace>();
		JSONArray annotations = obj.getJSONArray("annotations");
//		double threshold = 0.6; // to check which is the lowest rho value of the
//								// retrieved objects I want to accept
		for (int i = 0; i < annotations.length(); i++) {
			double rho = annotations.getJSONObject(i).getDouble(("rho"));
			if (rho >= threshold || annotations.length() == 1) { // i will
																	// consider
																	// the
																	// object
				String title = annotations.getJSONObject(i).getString("title");
				String uri = title;
				uri = uri.replace(" ", "_"); // every whitespace is replaced
												// with _
				uri = "http://dbpedia.org/resource/" + uri; // now the title is
															// converted to a
															// dbpedia uri
				SpottedPlace place = new SpottedPlace(title, uri);
				// TODO: add dbpedia categories
				places.add(place);
			}
		}
		return places;
	}

}
