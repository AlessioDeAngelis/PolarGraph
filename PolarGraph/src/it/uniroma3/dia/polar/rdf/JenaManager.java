package it.uniroma3.dia.polar.rdf;

import it.uniroma3.dia.polar.graph.model.Couple;
import it.uniroma3.dia.polar.graph.model.RecommendedObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class JenaManager {

	private final static Logger logger = LoggerFactory.getLogger(JenaManager.class);

	// TODO: you could substitute it with europeana object
	public List<RecommendedObject> queryEuropeana(String term) {
		String ontology_service = "http://europeana.ontotext.com/sparql";
		String endpoint = "otee:Endpoints";
		// String endpointsSparql =
		// "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX edm: <http://www.europeana.eu/schemas/edm/> PREFIX ore: <http://www.openarchives.org/ore/terms/> PREFIX dc: <http://purl.org/dc/elements/1.1/>  SELECT distinct ?proxy ?title ?subject ?mediaURL WHERE {     FILTER (contains(?subject, '"
		// + term
		// + "') || contains(?title, '"
		// + term
		// +
		// "')) ?resource ore:proxyIn ?proxy ; dc:title ?title ; dc:subject ?sunject; dc:creator ?creator ; dc:source ?source . ?proxy edm:isShownBy ?mediaURL .  } limit 50";
		term = term.replace("'", "");
		StringBuilder queryString = new StringBuilder();
		queryString.append("PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> ");
		queryString.append(" PREFIX edm: <http://www.europeana.eu/schemas/edm/> ");
		queryString.append(" PREFIX ore: <http://www.openarchives.org/ore/terms/> ");
		queryString.append(" PREFIX dc: <http://purl.org/dc/elements/1.1/> ");
		queryString.append("  SELECT distinct ?proxy ?creator ?mediaURL ?provider ?title ?source ?cho"); // at
																									// the
																									// moment
																									// we
																									// don't
																									// need
																									// to
																									// return
																									// the
																									// subject
		queryString.append(" WHERE { ?s dc:creator ?creator; ");
		queryString.append(" ore:proxyIn ?proxy; ");
		queryString.append(" dc:subject ?subject; ");
		queryString.append(" dc:title ?title;");
		queryString.append(" dc:title ?source;");
		queryString.append(" dc:type ?type.");
		queryString.append(" ?proxy edm:isShownBy ?mediaURL. ");
		queryString.append("   ?proxy edm:dataProvider ?provider. ");
		queryString.append(" ?proxy edm:aggregatedCHO ?cho. ");
		queryString.append(" {    ?s dc:title '" + term + "' .} UNION ");
		queryString.append(" {     ?s dc:subject '" + term + "' .} } LIMIT 100");

		// QueryExecution queryExecution =
		// QueryExecutionFactory.sparqlService(ontology_service,
		// String.format(endpointsSparql, endpoint));
		QueryExecution 	queryExecution = QueryExecutionFactory.sparqlService(ontology_service, String.format(queryString.toString(),endpoint));
		
		ResultSet results = queryExecution.execSelect();

		List<RecommendedObject> retrievedObjects = new ArrayList<RecommendedObject>();
		String prevUri = "";
		RecommendedObject retrievedObject = new RecommendedObject();

		while (results.hasNext()) {
			QuerySolution solution = results.next();
			String title = solution.get("title").toString();
			String uri = solution.get("proxy").toString();
			String mediaUrl = solution.get("mediaURL").toString();
			String creator = solution.get("creator").toString();
			String provider = solution.get("provider").toString();
			String source = solution.get("source").toString();
			String externalLink = "http://europeana.ontotext.com/resource?uri="+solution.get("cho").toString();

			if (!prevUri.equals(uri)) { // different object, create it and add
										// it to the collection
				retrievedObject = new RecommendedObject();
				retrievedObject.setName(title);
				retrievedObject.setUri(uri);
				retrievedObject.setMediaUrl(mediaUrl);
				retrievedObject.setCreator(creator);
				retrievedObject.setProvider(provider);
				retrievedObject.setWhy(term);
				retrievedObject.setSource(source);
				retrievedObject.setExternalLink(externalLink);
				retrievedObjects.add(retrievedObject);
			} else { // same object as before, update the creator values
				retrievedObject.setCreator(retrievedObject.getCreator() + " ; " + creator);
			}
			prevUri = uri;
		}

		return retrievedObjects;
	}

	/**
	 * QUERIES DBPEDIA AND RETURNS ALL THE PLACES THAT ARE GEOLOCALLY CLOSER TO
	 * THE GIVEN URI AND THAT SHARE A CATEGORY WITH IT
	 * */
	public List<String> findCloserPlacesFromDbpedia(String term) {
		String ontology_service = "http://dbpedia.org/sparql";
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
	
	/**
	 * QUERIES DBPEDIA AND RETURNS THE LAT AND LANG OF THE GIVEN URI. THE URI SHOULD BE ABOUT A PLACE
	 * */
	public Couple<String,String> retrieveLatLangFromDbpedia(String term) {
		String ontology_service = "http://dbpedia.org/sparql";
//		String ontology_service = "http://live.dbpedia.org/sparql";

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

		queryString.append("  SELECT ?lat ?lng");
		queryString.append(" WHERE { " + term + "  geo:lat ?lat. ");
		queryString.append(term + "  geo:long ?lng.");
		queryString.append("} LIMIT 1");

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ontology_service, queryString.toString());
		ResultSet results = queryExecution.execSelect();

		List<String> retrievedConcepts = new ArrayList<String>();
		Couple<String, String> latLng = new Couple<String, String>("", "");
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			String lat = ""+ solution.getLiteral("lat").getFloat();
			
			String lng = "" + solution.getLiteral("lng").getFloat();
			latLng = new Couple<String, String>(lat, lng);
		}

		return latLng;
	}

	public List<RecommendedObject> textQueryDbpedia(String dbpediaUri) {
		String ontology_service = "http://dbpedia.org/sparql";
		String endpoint = "otee:Endpoints";
		String endpointsSparql = "PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct  ?opera ?museum where { ?opera dbpedia-owl:museum ?museum. ?museum dbp-prop:location "
				+ "<" + dbpediaUri + ">" + "  } LIMIT 200";

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ontology_service,
				String.format(endpointsSparql, endpoint));
		ResultSet results = queryExecution.execSelect();

		List<RecommendedObject> retrievedObjects = new ArrayList<RecommendedObject>();
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			String title = solution.get("opera").toString();
			String uri = solution.get("museum").toString();
			// String mediaUrl = solution.get("mediaURL").toString();
			RecommendedObject retrievedObject = new RecommendedObject();
			retrievedObject.setName(title);
			retrievedObject.setUri(uri);
			// retrievedObject.setMediaUrl(mediaUrl);
			retrievedObjects.add(retrievedObject);
		}

		return retrievedObjects;
	}
	
	@Deprecated
	public String queryDbpediaForImageUrl(String dbpediaUri) {
		String ontology_service = "http://dbpedia.org/sparql";
		String endpoint = "otee:Endpoints";
		String endpointsSparql = "PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?mediaUrl { "
				+ dbpediaUri + " dbpedia-owl:thumbnail ?mediaUrl. } LIMIT 1";

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ontology_service,
				String.format(endpointsSparql, endpoint));
		ResultSet results = queryExecution.execSelect();

		List<RecommendedObject> retrievedObjects = new ArrayList<RecommendedObject>();
		String mediaUrl = "";
		if (results.hasNext()) {
			QuerySolution solution = results.next();
			mediaUrl = solution.get("mediaUrl").toString();
		}

		return mediaUrl;
	}
	
	/**
	 * Given a dbpedia uri, extracts extra infos. At the moment the extra infos are the mediaUrl and the external wikipedia link
	 **/
	public Map<String,String> queryDbpediaForExtraInfo(String dbpediaUri) {
		Map<String,String> dbpediaAttribute2value = new HashMap<String,String>();
		String ontology_service = "http://dbpedia.org/sparql";
		ontology_service = "http://192.168.127.22:8890/sparql";

		String endpoint = "otee:Endpoints";
		String endpointsSparql = "PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> PREFIX foaf: <http://xmlns.com/foaf/0.1/> select distinct ?mediaUrl ?externalLink { "
				+ dbpediaUri + " dbpedia-owl:thumbnail ?mediaUrl; foaf:isPrimaryTopicOf ?externalLink.} LIMIT 1";

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ontology_service,
				String.format(endpointsSparql, endpoint));
		ResultSet results = queryExecution.execSelect();

		String mediaUrl = "";
		String externalLink = "";
		if (results.hasNext()) {
			QuerySolution solution = results.next();
			mediaUrl = solution.get("mediaUrl").toString();
			externalLink = solution.get("externalLink").toString();
			
			dbpediaAttribute2value.put("mediaUrl",mediaUrl);
			dbpediaAttribute2value.put("externalLink", externalLink);
		}

		return dbpediaAttribute2value;
	}

	public static List<String> conceptQuery(String cm, String cx) {
		List<String> extractedConcepts = new ArrayList<>();
		List<String> heritageTypes = new ArrayList<>();

		heritageTypes.add("http://dbpedia.org/ontology/Abbey");
		heritageTypes.add("http://dbpedia.org/ontology/AmusementParkAttraction");
		heritageTypes.add("http://dbpedia.org/ontology/Archeologist");
		heritageTypes.add("http://dbpedia.org/ontology/Archipelago");
		heritageTypes.add("http://dbpedia.org/ontology/Architect");
		heritageTypes.add("http://dbpedia.org/ontology/ArchitecturalStructure");
		heritageTypes.add("http://dbpedia.org/ontology/Artist");
		heritageTypes.add("http://dbpedia.org/ontology/Artwork");
		heritageTypes.add("http://dbpedia.org/ontology/Book");
		heritageTypes.add("http://dbpedia.org/ontology/Building");
		heritageTypes.add("http://dbpedia.org/ontology/Castle");
		heritageTypes.add("http://dbpedia.org/ontology/Cave");
		heritageTypes.add("http://dbpedia.org/ontology/Church");
		heritageTypes.add("http://dbpedia.org/ontology/City");
		heritageTypes.add("http://dbpedia.org/ontology/Country");
		heritageTypes.add("http://dbpedia.org/ontology/Glacier");
		heritageTypes.add("http://dbpedia.org/ontology/Historian");
		heritageTypes.add("http://dbpedia.org/ontology/HistoricBuilding");
		heritageTypes.add("http://dbpedia.org/ontology/HistoricPlace");
		heritageTypes.add("http://dbpedia.org/ontology/HistoricalPeriod");
		heritageTypes.add("http://dbpedia.org/ontology/Island");
		heritageTypes.add("http://dbpedia.org/ontology/Lake");
		heritageTypes.add("http://dbpedia.org/ontology/Locality");
		heritageTypes.add("http://dbpedia.org/ontology/Monument");
		heritageTypes.add("http://dbpedia.org/ontology/Mosque");
		heritageTypes.add("http://dbpedia.org/ontology/Mountain");
		heritageTypes.add("http://dbpedia.org/ontology/Museum");
		heritageTypes.add("http://dbpedia.org/ontology/MusicalWork");
		heritageTypes.add("http://dbpedia.org/ontology/Opera");
		heritageTypes.add("http://dbpedia.org/ontology/Painter");
		heritageTypes.add("http://dbpedia.org/ontology/Painting");
		heritageTypes.add("http://dbpedia.org/ontology/Park");
		heritageTypes.add("http://dbpedia.org/ontology/Photographer");
		heritageTypes.add("http://dbpedia.org/ontology/Rive");
		heritageTypes.add("http://dbpedia.org/ontology/Sculptor");
		heritageTypes.add("http://dbpedia.org/ontology/Sculpture");
		heritageTypes.add("http://dbpedia.org/ontology/Settlement");
		heritageTypes.add("http://dbpedia.org/ontology/Stadium");
		heritageTypes.add("http://dbpedia.org/ontology/Synagogue");
		heritageTypes.add("http://dbpedia.org/ontology/Temple");
		heritageTypes.add("http://dbpedia.org/ontology/Tower");
		heritageTypes.add("http://dbpedia.org/ontology/Volcano");
		heritageTypes.add("http://dbpedia.org/ontology/WaterTower");
		heritageTypes.add("http://dbpedia.org/ontology/WorldHeritageSite");
		heritageTypes.add("http://dbpedia.org/ontology/Work");
		heritageTypes.add("http://dbpedia.org/class/yago/YagoPermanentlyLocatedEntity");

		String heritageTypesQueryString = " {" + " ?c a <" + heritageTypes.get(0) + ">.} ";
		for (int i = 1; i < heritageTypes.size(); i++) {
			heritageTypesQueryString += " UNION {" + " ?c a <" + heritageTypes.get(i) + ">.} ";
		}

		String ontology_service = "http://dbpedia.org/sparql";
		// String ontology_service = "http://live.dbpedia.org/sparql";
		String endpoint = "otee:Endpoints";
		/**
		 * query 1-5 ---> c is a place 7-11 ---> c can be everything 12-15 --->
		 * c can be one of a subset of rdf:types 16-17 ---> c can be one of a
		 * subset if rdf:types and there is even cy 18-19 ---> p1=p2 and c can
		 * be one of a subset if rdf:types 20 ---> p1=p2=dcterms:subject and c
		 * can be one of a subset if rdf:types 21 - 22 ---> direct mentions
		 * */
		String query1 = "PREFIX dcterms:<http://purl.org/dc/terms/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 ?c. " + cx + " ?p2 ?c. ?c rdf:type dbpedia-owl:Place.} LIMIT 50 ";
		String query2 = "PREFIX dcterms:<http://purl.org/dc/terms/> PREFIX  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 " + cx + ". ?c ?p2 " + cx + ". ?c rdf:type dbpedia-owl:Place.} LIMIT 50 ";
		String query3 = "PREFIX dcterms:<http://purl.org/dc/terms/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 " + cx + ". " + cx + " ?p2 ?c. ?c rdf:type dbpedia-owl:Place.} LIMIT 50 ";
		String query4 = "PREFIX dcterms:<http://purl.org/dc/terms/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cx + " ?p1 " + cm + ". " + cm + " ?p2 ?c. ?c rdf:type dbpedia-owl:Place.} LIMIT 50 ";
		String query5 = "PREFIX dcterms:<http://purl.org/dc/terms/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 ?c. ?c ?p2 ?cy. ?cy ?p3 " + cx + ". ?c rdf:type dbpedia-owl:Place.} LIMIT 50 ";

		String query6 = "PREFIX dcterms:<http://purl.org/dc/terms/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 ?c. " + cx + " ?p2 ?c.} LIMIT 50 ";
		String query7 = "PREFIX dcterms:<http://purl.org/dc/terms/> PREFIX  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 " + cx + ". ?c ?p2 " + cx + ". } LIMIT 50 ";
		String query8 = "PREFIX dcterms:<http://purl.org/dc/terms/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 " + cx + ". " + cx + " ?p2 ?c.} LIMIT 50 ";
		String query9 = "PREFIX dcterms:<http://purl.org/dc/terms/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cx + " ?p1 " + cm + ". " + cm + " ?p2 ?c. } LIMIT 50 ";
		String query10 = "PREFIX dcterms:<http://purl.org/dc/terms/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 ?c. ?c ?p2 ?cy. ?cy ?p3 " + cx + ". } LIMIT 50 ";

		String query11 = "PREFIX dcterms:<http://purl.org/dc/terms/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 ?c. " + cx + " ?p2 ?c. " + heritageTypesQueryString + " } LIMIT 50 ";
		String query12 = "PREFIX dcterms:<http://purl.org/dc/terms/> PREFIX  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 " + cx + ". ?c ?p2 " + cx + ". " + heritageTypesQueryString + " } LIMIT 50 ";
		String query13 = "PREFIX dcterms:<http://purl.org/dc/terms/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 " + cx + ". " + cx + " ?p2 ?c. " + heritageTypesQueryString + " } LIMIT 50 ";
		String query14 = "PREFIX dcterms:<http://purl.org/dc/terms/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cx + " ?p1 " + cm + ". " + cm + " ?p2 ?c. } LIMIT 50 ";
		String query15 = "PREFIX dcterms:<http://purl.org/dc/terms/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 ?c. ?c ?p2 ?cy. ?cy ?p3 " + cx + ". " + heritageTypesQueryString + " } LIMIT 50 ";
		String query16 = "PREFIX dcterms:<http://purl.org/dc/terms/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 ?cy. ?cy ?p2 ?c. " + cx + " ?p3 ?c. " + heritageTypesQueryString + " } LIMIT 50 ";

		String query17 = "PREFIX dcterms:<http://purl.org/dc/terms/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " dcterms:subject ?cy. ?c dcterms:subject ?cy. " + heritageTypesQueryString + " } LIMIT 50 ";
		String query18 = "PREFIX dcterms:<http://purl.org/dc/terms/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 ?cy. ?c ?p1 ?cy. " + heritageTypesQueryString + " } LIMIT 50 ";
		String query19 = "PREFIX dcterms:<http://purl.org/dc/terms/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 ?cy. " + cx + " ?p1 ?cy. ?c ?p1 ?cy. " + heritageTypesQueryString + " } LIMIT 50 ";
		String query20 = "PREFIX dcterms:<http://purl.org/dc/terms/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm
				+ " dcterms:subject ?cy. "
				+ cx
				+ " ?p1 ?cy. ?c dcterms:subject  ?cy. "
				+ heritageTypesQueryString
				+ " } LIMIT 50 ";

		String query21 = "PREFIX dcterms:<http://purl.org/dc/terms/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?cx WHERE{ "
				+ cm + " ?p1 " + cx + ". } LIMIT 50 ";
		String query22 = "PREFIX dcterms:<http://purl.org/dc/terms/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?cx WHERE{ "
				+ cx + " ?p1 " + cm + ". } LIMIT 50 ";

		List<String> allQueries = new ArrayList<>();
		// allQueries.add(query1);
		// allQueries.add(query2);
		// allQueries.add(query3);
		// allQueries.add(query4);
		// allQueries.add(query5);
		allQueries.add(query6);
		allQueries.add(query7);
		allQueries.add(query8);
		allQueries.add(query9);
		allQueries.add(query10);
		allQueries.add(query11);

		// allQueries.add(query12);
		// allQueries.add(query13);
		// allQueries.add(query14);
		// allQueries.add(query15);

		allQueries.add(query16);
		allQueries.add(query17);
		// allQueries.add(query18);
		// allQueries.add(query19);
		// allQueries.add(query20);
		allQueries.add(query21);
		allQueries.add(query22);

		StringBuilder finalResult = new StringBuilder();
		QueryExecution queryExecution = null;
		int i = 1;
		for (String query : allQueries) {
			logger.info(query);

			finalResult.append("QUERY " + i + " : " + query + "\n");
			finalResult.append("==================");
			queryExecution = QueryExecutionFactory.sparqlService(ontology_service, String.format(query, endpoint));
			ResultSet results = queryExecution.execSelect();

			while (results.hasNext()) {
				QuerySolution solution = results.next();
				String concept = "";
				if (solution.get("c") != null) {
					concept = solution.get("c").toString();
				} else if (solution.get("cx") != null) {
					concept = solution.get("cx").toString();
				}
				finalResult.append("\n---------------- \n" + concept);
				extractedConcepts.add(concept);
			}
			finalResult.append("\n\n");
			i++;
		}
		// System.out.println(finalResult.toString());
		return extractedConcepts;
	}
	

	public static void main(String[] args) {
		// String cm = "dbpedia:Girl_with_a_Pearl_Earring";
		// String cx = "dbpedia:Johannes_Vermeer";
		String cm = "<http://dbpedia.org/resource/Colosseum>";
		String cx = "<http://dbpedia.org/resource/Vespasian>";
		List<String> objs = conceptQuery(cm, cx);
		System.out.println(objs.size());
	}
}
