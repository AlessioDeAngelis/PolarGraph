package it.uniroma3.dia.polar.disambiguator;

import it.uniroma3.dia.polar.controller.PropertiesManager;
import it.uniroma3.dia.polar.graph.model.Category;
import it.uniroma3.dia.polar.graph.model.Couple;
import it.uniroma3.dia.polar.graph.model.Location;
import it.uniroma3.dia.polar.graph.model.PolarPlace;
import it.uniroma3.dia.polar.parser.JSONParser;
import it.uniroma3.dia.polar.parser.XMLParser;
import it.uniroma3.dia.polar.rdf.JenaManager;
import it.uniroma3.dia.polar.rest.RestManager;

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
		double maxDistance = 0.005;
		for (Category category : placeToDisambiguate.getCategories()) {
			if (category.getName() != null && category.getName().equals("City")) {
				maxDistance = 2;
			}
		}
		SpottedPlace correctPlace = null;

		String term = placeToDisambiguate.getName();
		String termSanitized = term.replace(".", "+"); // remove the dots
		String tagMeJsonOutput = this.restManager.queryTagMe(termSanitized);
		List<SpottedPlace> tagMePlaces = this.jsonParser.parseTagMe(tagMeJsonOutput,0.1);
		for (SpottedPlace tagMePlace : tagMePlaces) {
			String placeDbpediaUri = tagMePlace.getUri();
			// we cannot do refinement based on the coordinates of the tag if
			// there are no geo coordinates attacched to the place
			if (placeToDisambiguate.getLocation() == null) {
				correctPlace = tagMePlace;
			}
			// let's check if the geo coordinates of the place retrieved by
			// TagMeService is close to the location of the placeToDisambiguate
			//otherwise the heuristics says that the concepts coincide if they have exactly the same name and the max distance is higher 
			else if (placeDbpediaUri != null && !placeDbpediaUri.equals("") && !placeDbpediaUri.equals(" ")) {
				Couple<String, String> latLng = jenaManager.retrieveLatLangFromDbpedia(placeDbpediaUri);
				if (latLng != null && !latLng.getFirst().equals("") && !latLng.getSecond().equals("")) {
					double lat1 = Double.parseDouble(latLng.getFirst());
					double lng1 = Double.parseDouble(latLng.getSecond());
					double lat2 = placeToDisambiguate.getLocation().getLatitude();
					double lng2 = placeToDisambiguate.getLocation().getLongitude();
					// double maxDistance = 0.005;
					boolean nameEqualityAndGeoCloseness = (isTheSamePlaceByCoordinates(lat1, lng1, lat2, lng2, 3) && placeDbpediaUri.equals("http://dbpedia.org/resource/"+termSanitized.replaceAll(" ", "_")));
					if (isTheSamePlaceByCoordinates(lat1, lng1, lat2, lng2, maxDistance) || nameEqualityAndGeoCloseness) {
						correctPlace = tagMePlace;
						break;
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
		return placeToDisambiguate;
	}

	private boolean isTheSamePlaceByCoordinates(double lat1, double lng1, double lat2, double lng2, double maxDistance) {
		return Math.abs(lat1 - lat2) < maxDistance && Math.abs(lng1 - lng2) < maxDistance;
	}

}
