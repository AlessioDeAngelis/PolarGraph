package it.uniroma3.dia.cicero.rdf;

import it.uniroma3.dia.cicero.graph.model.Couple;
import it.uniroma3.dia.cicero.graph.model.RecommendedObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.atlas.web.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class CopyOfJenaManager {

	private final static Logger logger = LoggerFactory.getLogger(CopyOfJenaManager.class);


	
	/**
	 * QUERIES DBPEDIA AND RETURNS ALL THE PLACES THAT ARE GEOLOCALLY CLOSER TO
	 * THE GIVEN URI AND THAT SHARE A CATEGORY WITH IT
	 * */
	public static List<String> findCloserPlacesFromDbpedia(String term) {
		String ontology_service = "http://192.168.127.22:8890/sparql";
		String endpoint = "otee:Endpoints";
		term = "<" + term + ">";
		StringBuilder queryString = new StringBuilder();
		queryString.append("PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> ");
		queryString.append(" PREFIX dbp-prop: <http://dbpedia.org/property/> ");
		queryString.append(" PREFIX dbpedia: <http://dbpedia.org/resource/> ");
		queryString.append(" PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> ");
		queryString.append(" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ");
		queryString.append(" PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> ");
		queryString.append("  PREFIX dcterms:<http://purl.org/dc/terms/> ");

		queryString.append("  SELECT distinct ?concept");
		queryString.append(" WHERE { " + term + "  geo:lat ?uriLat. ");
		queryString.append(term + "  geo:long ?uriLong.");
		queryString.append(term + " dcterms:subject ?sub.");
		queryString.append("?concept geo:lat ?lat. ");
		queryString.append("?concept geo:long ?long. ");
		queryString.append("?concept dcterms:subject ?sub. ");
		queryString.append("FILTER(?lat - ?uriLat <= 0.05 && ?uriLat - ?lat <= 0.05 &&");
		queryString.append("?long - ?uriLong <= 0.05 && ?uriLong - ?long <= 0.05 &&");
		queryString.append("?concept!=" + term + ").} LIMIT 10");

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ontology_service, queryString.toString());
		ResultSet results = queryExecution.execSelect();

		List<String> retrievedConcepts = new ArrayList<String>();

		while (results.hasNext()) {
			QuerySolution solution = results.next();
			String concept = solution.get("concept").toString();
			retrievedConcepts.add(concept);
		}

		return retrievedConcepts;
	}

	public static void main(String[] args) {
		// String cm = "dbpedia:Girl_with_a_Pearl_Earring";
		// String cx = "dbpedia:Johannes_Vermeer";
		String cm = "http://dbpedia.org/resource/Moholt";
		String cx = "http://dbpedia.org/resource/Vespasian";
		List<String> ss = findCloserPlacesFromDbpedia(cm);
		for(String s : ss) System.out.println(s);
//		List<String> objs = JenaManager.conceptQuery(cm, cx);
//		System.out.println(objs.size());
	}
}
