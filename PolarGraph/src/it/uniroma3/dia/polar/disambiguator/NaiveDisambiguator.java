package it.uniroma3.dia.polar.disambiguator;

import it.uniroma3.dia.polar.controller.PropertiesManager;
import it.uniroma3.dia.polar.graph.model.Location;
import it.uniroma3.dia.polar.graph.model.PolarPlace;
import it.uniroma3.dia.polar.parser.JSONParser;
import it.uniroma3.dia.polar.parser.XMLParser;
import it.uniroma3.dia.polar.rest.RestManager;

import java.util.List;

import com.google.inject.Inject;

/**
 * It uses mostly geonames and dbpedia spotlight
 * */
public class NaiveDisambiguator implements Disambiguator {

	private final RestManager restManager;
	private final PropertiesManager propertiesController;
	private final XMLParser xmlParser;
	private final JSONParser jsonParser;

	@Inject
	public NaiveDisambiguator(final RestManager restManager,
			final PropertiesManager propertiesController,
			final XMLParser xmlParser, final JSONParser jsonParser) {
		this.restManager = restManager;
		this.propertiesController = propertiesController;
		this.xmlParser = xmlParser;
		this.jsonParser = jsonParser;
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
		SpottedPlace correctPlace = null;

		String term = placeToDisambiguate.getName();
		String termSanitized = term.replace(".", "+"); // remove the dots
		String tagMeJsonOutput = this.restManager.queryTagMe(termSanitized);
		List<SpottedPlace> tagMePlaces = this.jsonParser
				.parseTagMe(tagMeJsonOutput);
		if (tagMePlaces != null && tagMePlaces.size() > 0) {
			correctPlace = tagMePlaces.get(0);
		} else {
			String geonameByNameXMLOutput = this.restManager
					.queryGeonamesByName(termSanitized);
			// query geonames service by name
			List<SpottedPlace> geonamesPlaces = this.xmlParser
					.parseGeonamesRDF(geonameByNameXMLOutput);
			if (geonamesPlaces != null && geonamesPlaces.size() > 0) {
				correctPlace = geonamesPlaces.get(0);
			} else {
				//TODO: as soon as spotlight is available you can uncomment the following lines
				// query spotlight service
//				String spotligthXMLOutput = this.restManager
//						.querySpotlightByName(termSanitized);
//				List<SpottedPlace> spotlightPlaces = this.xmlParser
//						.parseSpotlight(spotligthXMLOutput);
				List<SpottedPlace> spotlightPlaces = null; //remove this if spotlight is available again
				if (spotlightPlaces != null && spotlightPlaces.size() > 0) {
					correctPlace = spotlightPlaces.get(0);
				} else {
					// query geonames service by lat and long
					String lat = ""
							+ placeToDisambiguate.getLocation().getLatitude();
					String lng = ""
							+ placeToDisambiguate.getLocation().getLongitude();
					if (!lat.equals("") && !lng.equals("")) {
						String geonameByLatLngXMLOutput = this.restManager
								.queryGeonamesByLatLng(lat, lng);
						geonamesPlaces = this.xmlParser
								.parseGeonamesRDF(geonameByLatLngXMLOutput);
						if (geonamesPlaces != null && geonamesPlaces.size() > 0) {
							correctPlace = geonamesPlaces.get(0);
						}
						for (SpottedPlace place : geonamesPlaces) {
							if (place.getName() != null
									&& place.getName().contains(term)) {
								correctPlace = place;
								break;
							}
						}
					}
				}
			}
		}
		// update the original polar place after the disambiguation
		if (correctPlace != null) {
			if (correctPlace.getName() != null
					&& !correctPlace.getName().equals("")) {
				placeToDisambiguate.setName(correctPlace.getName());
			}
			if (correctPlace.getUri() != null
					&& !correctPlace.getUri().equals("")) {
				placeToDisambiguate.setUri(correctPlace.getUri());
			}
			Location placeLocation = placeToDisambiguate.getLocation();
			if (correctPlace.getLatitude() != null
					&& !correctPlace.getLatitude().equals("")) {
				placeLocation.setLatitude(Double.parseDouble(correctPlace
						.getLatitude()));
			}
			if (correctPlace.getLongitude() != null
					&& !correctPlace.getLongitude().equals("")) {
				placeLocation.setLongitude(Double.parseDouble(correctPlace
						.getLongitude()));
			}
		}
		return placeToDisambiguate;
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
	// @Override
	public PolarPlace disambiguatePlacebackup(PolarPlace placeToDisambiguate) {
		String term = placeToDisambiguate.getName();
		String termSanitized = term.replace(".", "+"); // remove the dots
		String geonameByNameXMLOutput = this.restManager
				.queryGeonamesByName(termSanitized);
		SpottedPlace correctPlace = null;
		// query geonames service by name
		List<SpottedPlace> geonamesPlaces = this.xmlParser
				.parseGeonamesRDF(geonameByNameXMLOutput);
		if (geonamesPlaces != null && geonamesPlaces.size() > 0) {
			correctPlace = geonamesPlaces.get(0);
		} else {
			// query spotlight service
			String spotligthXMLOutput = this.restManager
					.querySpotlightByName(termSanitized);
			List<SpottedPlace> spotlightPlaces = this.xmlParser
					.parseSpotlight(spotligthXMLOutput);
			if (spotlightPlaces != null && spotlightPlaces.size() > 0) {
				correctPlace = spotlightPlaces.get(0);
			} else {
				// query geonames service by lat and long
				String lat = ""
						+ placeToDisambiguate.getLocation().getLatitude();
				String lng = ""
						+ placeToDisambiguate.getLocation().getLongitude();
				if (!lat.equals("") && !lng.equals("")) {
					String geonameByLatLngXMLOutput = this.restManager
							.queryGeonamesByLatLng(lat, lng);
					geonamesPlaces = this.xmlParser
							.parseGeonamesRDF(geonameByLatLngXMLOutput);
					if (geonamesPlaces != null && geonamesPlaces.size() > 0) {
						correctPlace = geonamesPlaces.get(0);
					}
					for (SpottedPlace place : geonamesPlaces) {
						if (place.getName() != null
								&& place.getName().contains(term)) {
							correctPlace = place;
							break;
						}
					}
				}
			}
		}
		// update the original polar place after the disambiguation
		if (correctPlace != null) {
			if (correctPlace.getName() != null
					&& !correctPlace.getName().equals("")) {
				placeToDisambiguate.setName(correctPlace.getName());
			}
			if (correctPlace.getUri() != null
					&& !correctPlace.getUri().equals("")) {
				placeToDisambiguate.setUri(correctPlace.getUri());
			}
			Location placeLocation = placeToDisambiguate.getLocation();
			if (correctPlace.getLatitude() != null
					&& !correctPlace.getLatitude().equals("")) {
				placeLocation.setLatitude(Double.parseDouble(correctPlace
						.getLatitude()));
			}
			if (correctPlace.getLongitude() != null
					&& !correctPlace.getLongitude().equals("")) {
				placeLocation.setLongitude(Double.parseDouble(correctPlace
						.getLongitude()));
			}
		}
		return placeToDisambiguate;
	}

}
