package it.uniroma3.dia.cicero.disambiguator;

import it.uniroma3.dia.cicero.controller.PropertiesManager;
import it.uniroma3.dia.cicero.graph.model.Category;
import it.uniroma3.dia.cicero.graph.model.Couple;
import it.uniroma3.dia.cicero.graph.model.Location;
import it.uniroma3.dia.cicero.graph.model.PolarPlace;
import it.uniroma3.dia.cicero.parser.JSONParser;
import it.uniroma3.dia.cicero.parser.XMLParser;
import it.uniroma3.dia.cicero.rdf.JenaManager;
import it.uniroma3.dia.cicero.rest.RestManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.google.inject.Inject;

//TODO: controllare con jena manager se la lat e long del posto corrispondono

public class SemanticPlacesDisambiguator implements Disambiguator {

	private final RestManager restManager;
	private final PropertiesManager propertiesController;
	private final XMLParser xmlParser;
	private final JSONParser jsonParser;
	private final JenaManager jenaManager;

	@Inject
	public SemanticPlacesDisambiguator(final RestManager restManager, final PropertiesManager propertiesController,
			final XMLParser xmlParser, final JSONParser jsonParser, final JenaManager jenaManager) {
		this.restManager = restManager;
		this.propertiesController = propertiesController;
		this.xmlParser = xmlParser;
		this.jsonParser = jsonParser;
		this.jenaManager = jenaManager;
	}

	/**
	 * It uses services as TagMe and Geonames to try to disambiguate the term of
	 * the place and retrieve a semantic dbpedia uri. Furthermore it queries
	 * dbpedia to check if the place retrieved is actually close to the spot
	 * where the user tagged himself
	 * 
	 * @param placeToDisambiguate
	 *            is the name of the place that you want to disambiguate
	 * */
	@Override
	public PolarPlace disambiguatePlace(PolarPlace placeToDisambiguate) {
		double maxDistance = 0.1;
		for (Category category : placeToDisambiguate.getCategories()) {
			if (category.getName() != null && category.getName().equals("City")) {
				maxDistance = 2;
			}
		}
		SpottedPlace correctPlace = null;

		String term = placeToDisambiguate.getName();
		String termSanitized = "";
		try {
			termSanitized = URLEncoder.encode(term, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String[] languages = { "it", "en" };
		// firstly try to query tag me with italian language, then english
		try{
		for (String language : languages) {
			String tagMeJsonOutput = this.restManager.queryTagMe(termSanitized, language);
			List<SpottedPlace> tagMePlaces = this.jsonParser.parseTagMe(tagMeJsonOutput, 0.1);
			for (SpottedPlace tagMePlace : tagMePlaces) {
				String placeDbpediaUri = tagMePlace.getUri();
				// we cannot do refinement based on the coordinates of the tag
				// if
				// there are no geo coordinates attacched to the place
				if (placeToDisambiguate.getLocation() == null) {
					correctPlace = tagMePlace;
				}
				// let's check if the geo coordinates of the place retrieved by
				// TagMeService is close to the location of the
				// placeToDisambiguate
				// otherwise the heuristics says that the concepts coincide if
				// they have exactly the same name and the max distance is
				// higher
				else if (placeDbpediaUri != null && !placeDbpediaUri.equals("") && !placeDbpediaUri.equals(" ")) {
					Couple<String, String> latLng = jenaManager.retrieveLatLangFromDbpedia(placeDbpediaUri);
					if (latLng != null && !latLng.getFirst().equals("") && !latLng.getSecond().equals("")) {
						double latFromDbpedia = Double.parseDouble(latLng.getFirst());// lat
						// from
						// dbpedia
						double lngFromDbpedia = Double.parseDouble(latLng.getSecond()); // long
						// from
						// dbpedia
						double lat2 = placeToDisambiguate.getLocation().getLatitude(); // lat
																						// from
																						// the
																						// social
																						// tag
						double lng2 = placeToDisambiguate.getLocation().getLongitude();// long
																						// from
																						// the
																						// social
																						// long
						double placeToDisambiguateManDistance = manhattanDistance(latFromDbpedia, lngFromDbpedia, lat2,
								lng2);
						// double maxDistance = 0.005;
						boolean nameEqualityAndGeoCloseness = (isTheSamePlaceByCoordinates(latFromDbpedia,
								lngFromDbpedia, lat2, lng2, 3) && placeDbpediaUri.equals("http://dbpedia.org/resource/"
								+ termSanitized.replaceAll(" ", "_")));
						if (isTheSamePlaceByCoordinates(latFromDbpedia, lngFromDbpedia, lat2, lng2, maxDistance)
								|| nameEqualityAndGeoCloseness) {
							if (correctPlace == null) {
								correctPlace = tagMePlace;
								correctPlace.setLatitude("" + latFromDbpedia);
								correctPlace.setLongitude("" + lngFromDbpedia);
							} else {// choose the tag me place that minimize the
									// man distance
								if (correctPlace.getLatitude() != null && correctPlace.getLongitude() != null
										&& !correctPlace.getLatitude().equals("")
										&& !correctPlace.getLongitude().equals("")) {
									double correctPlaceManDistance = manhattanDistance(latFromDbpedia, lngFromDbpedia,
											Double.parseDouble(correctPlace.getLatitude()),
											Double.parseDouble(correctPlace.getLongitude()));
									if (placeToDisambiguateManDistance < correctPlaceManDistance) {
										correctPlace = tagMePlace;
									}
								}

							}
						}
					}
				}
			}
		}
		// TagMe service didn't work, let's try with geonames
		if (correctPlace == null) {
			String geonameByNameXMLOutput = this.restManager.queryGeonamesByName(termSanitized);
			// query geonames service by name
			List<SpottedPlace> geonamesPlaces = this.xmlParser.parseGeonamesRDF(geonameByNameXMLOutput);
			for (SpottedPlace geonamesPlace : geonamesPlaces) {
				double lat1 = Double.parseDouble(geonamesPlace.getLatitude());
				double lng1 = Double.parseDouble(geonamesPlace.getLongitude());
				double lat2 = placeToDisambiguate.getLocation().getLatitude();
				double lng2 = placeToDisambiguate.getLocation().getLongitude();
				if (isTheSamePlaceByCoordinates(lat1, lng1, lat2, lng2, maxDistance)) {
					correctPlace = geonamesPlace;
					break;
				}
			}
		}

		// else {
		if (correctPlace == null) {
			// // query geonames service by lat and long
			String lat = "" + placeToDisambiguate.getLocation().getLatitude();
			String lng = "" + placeToDisambiguate.getLocation().getLongitude();
			if (!lat.equals("") && !lng.equals("")) {
				String geonameByLatLngXMLOutput = this.restManager.queryGeonamesByLatLng(lat, lng);
				List<SpottedPlace> geonamesPlaces = this.xmlParser.parseGeonamesRDF(geonameByLatLngXMLOutput);
				if (geonamesPlaces != null && geonamesPlaces.size() > 0) {
					correctPlace = geonamesPlaces.get(0);
				}
				for (SpottedPlace place : geonamesPlaces) {
					if (place.getName() != null && place.getName().contains(term)) {
						correctPlace = place;
						break;
					}
				}
			}
		}

		// }

		// update the original polar place after the disambiguation
		if (correctPlace != null) {
			if (correctPlace.getName() != null && !correctPlace.getName().equals("")) {
				placeToDisambiguate.setName(correctPlace.getName());
			}
			if (correctPlace.getUri() != null && !correctPlace.getUri().equals("")) {
				placeToDisambiguate.setUri(correctPlace.getUri());
			}
			Location placeLocation = placeToDisambiguate.getLocation();
			if (correctPlace.getLatitude() != null && !correctPlace.getLatitude().equals("")) {
				placeLocation.setLatitude(Double.parseDouble(correctPlace.getLatitude()));
			}
			if (correctPlace.getLongitude() != null && !correctPlace.getLongitude().equals("")) {
				placeLocation.setLongitude(Double.parseDouble(correctPlace.getLongitude()));
			}
		}
		}catch(Exception e){
			
		}
		return placeToDisambiguate;
	}

	private boolean isTheSamePlaceByCoordinates(double lat1, double lng1, double lat2, double lng2, double maxDistance) {
		return (Math.abs(lat1 - lat2) + Math.abs(lng1 - lng2)) < maxDistance;
	}

	private double manhattanDistance(double lat1, double lng1, double lat2, double lng2) {
		return (Math.abs(lat1 - lat2) + Math.abs(lng1 - lng2));
	}

}
