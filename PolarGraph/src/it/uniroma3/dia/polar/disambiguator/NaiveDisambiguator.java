package it.uniroma3.dia.polar.disambiguator;

import it.uniroma3.dia.polar.controller.PropertiesController;
import it.uniroma3.dia.polar.graph.model.Location;
import it.uniroma3.dia.polar.graph.model.PolarPlace;
import it.uniroma3.dia.polar.rest.RestManager;

import java.util.List;

import com.google.inject.Inject;

/**
 * It uses mostly geonames and dbpedia spotlight
 * */
public class NaiveDisambiguator implements Disambiguator {

	private final RestManager restManager;
	private final PropertiesController propertiesController;
	private final XMLParser xmlParser;

	@Inject
	public NaiveDisambiguator(final RestManager restManager, final PropertiesController propertiesController,
			final XMLParser xmlParser) {
		this.restManager = restManager;
		this.propertiesController = propertiesController;
		this.xmlParser = xmlParser;
	}

	/**
	 * The naive disambiguator takes the first result of the list of the places
	 * spotted with the Geonames service by name. If there is no result it
	 * queries dbpedia spotlight and takes the first result. If the result is
	 * empty is the time of Geonames by lat & long. This service is not as
	 * accurate as the others but it will highly return a place. To achieve
	 * better accuracy the disambiguator chooses the place that is more likely
	 * to be the correct one by just comparing the string.
	 * 
	 * @param term
	 *            is the name of the place that you want to disambiguate
	 * */
	@Override
	public PolarPlace disambiguatePlace(PolarPlace placeToDisambiguate) {
		String term = placeToDisambiguate.getName();
		String termSanitized = term.replace(".", "+"); // remove the dots
		String geonameByNameXMLOutput = queryGeonamesByName(termSanitized);
		SpottedPlace correctPlace = null;
		// query geonames service by name
		List<SpottedPlace> geonamesPlaces = this.xmlParser.parseGeonamesRDF(geonameByNameXMLOutput);
		if (geonamesPlaces != null && geonamesPlaces.size() > 0) {
			correctPlace = geonamesPlaces.get(0);
		} else {
			// query spotlight service
			String spotligthXMLOutput = querySpotlightByName(termSanitized);
			List<SpottedPlace> spotlightPlaces = this.xmlParser.parseSpotlight(spotligthXMLOutput);
			if (spotlightPlaces != null && spotlightPlaces.size() > 0) {
				correctPlace = spotlightPlaces.get(0);
			} else {
				// query geonames service by lat and long
				String lat = "" + placeToDisambiguate.getLocation().getLatitude();
				String lng = "" + placeToDisambiguate.getLocation().getLongitude();
				if (!lat.equals("") && !lng.equals("")) {
					String geonameByLatLngXMLOutput = queryGeonamesByLatLng(lat, lng);
					geonamesPlaces = this.xmlParser.parseGeonamesRDF(geonameByLatLngXMLOutput);
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
		}
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

	private String queryGeonamesByName(String name) {
		String urlString = "http://api.geonames.org/search?q=" + name + "&username=alessio&type=RDF";
		String requestMethod = "GET";
		String requestProperty = "application/xml";
		String output = this.restManager.restOperation(urlString, requestMethod, requestProperty);
		return output;
	}

	private String queryGeonamesByLatLng(String lat, String lng) {
		RestManager restManager = new RestManager();
		String urlString = "http://api.geonames.org/findNearbyWikipedia?lat=" + lat + "&lng=" + lng
				+ "&username=alessio&type=RDF";
		String requestMethod = "GET";
		String requestProperty = "application/xml";
		String output = restManager.restOperation(urlString, requestMethod, requestProperty);
		return output;
	}

	private String querySpotlightByName(String name) {
		RestManager restManager = new RestManager();
		String urlString = "http://spotlight.sztaki.hu:2222/rest/annotate/?text="
				+ name.replace(" ", "+").replace(".", "+");
		String requestMethod = "GET";
		String requestProperty = "application/xml";
		String output = restManager.restOperation(urlString, requestMethod, requestProperty);
		return output;
	}

}
