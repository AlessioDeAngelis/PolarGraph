package it.uniroma3.it.dia.polar.rest;

import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

public class GeoNamesTest {
	public static void main(String[] args) {
		WebService.setUserName("alessio");
		ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
		  searchCriteria.setQ("moholt");
		  ToponymSearchResult searchResult = null;
		try {
			searchResult = WebService.search(searchCriteria);
		} catch (Exception e) {
			e.printStackTrace();
		}
		  for (Toponym toponym : searchResult.getToponyms()) {
		     System.out.println(toponym.getName()+" "+ toponym.getCountryName() + " " +toponym.toString());
		  }
	}
}
