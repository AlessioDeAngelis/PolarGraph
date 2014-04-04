package it.uniroma3.dia.polar.rdf;

import it.uniroma3.dia.polar.graph.model.RecommendedObject;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class JenaManager {
	public List<RecommendedObject> textQueryEuropeana(String term) {
		String ontology_service = "http://europeana.ontotext.com/sparql";
		String endpoint = "otee:Endpoints";
		String endpointsSparql = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX edm: <http://www.europeana.eu/schemas/edm/> PREFIX ore: <http://www.openarchives.org/ore/terms/> PREFIX dc: <http://purl.org/dc/elements/1.1/>  SELECT distinct ?proxy ?title ?subject ?mediaURL WHERE {     FILTER (contains(?subject, '"
				+ term
				+ "') || contains(?title, '"
				+ term
				+ "')) ?resource ore:proxyIn ?proxy ; dc:title ?title ; dc:subject ?sunject; dc:creator ?creator ; dc:source ?source . ?proxy edm:isShownBy ?mediaURL .  } limit 50";
		// endpointsSparql =
		// " PREFIX edm: <http://www.europeana.eu/schemas/edm/> PREFIX ore: <http://www.openarchives.org/ore/terms/> PREFIX dc: <http://purl.org/dc/elements/1.1/>  SELECT ?title ?mediaURL ?creator ?source WHERE {     FILTER regex(?title, 'Gioconda') ?resource dc:title ?title ; dc:creator ?creator ; dc:source ?source .  }";
		// endpointsSparql =
		// " PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX ore: <http://www.openarchives.org/ore/terms/> PREFIX dc: <http://purl.org/dc/elements/1.1/> SELECT distinct ?s ?subject ?title WHERE { ?s rdf:type ore:Proxy; dc:subject ?subject ; dc:title ?title.  FILTER ( regex ( str(?subject), 'Geiranger') ) } LIMIT 100";
		// endpointsSparql =
		// " PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX ore: <http://www.openarchives.org/ore/terms/> PREFIX dc: <http://purl.org/dc/elements/1.1/> SELECT distinct ?s ?subject ?title WHERE { ?s dc:subject ?subject ; dc:title ?title.  FILTER ( regex ( str(?subject), 'Colosseo') ) } LIMIT 100";

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(
				ontology_service, String.format(endpointsSparql, endpoint));
		ResultSet results = queryExecution.execSelect();

		List<RecommendedObject> retrievedObjects = new ArrayList<RecommendedObject>();
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			String title = solution.get("title").toString();
			String uri = solution.get("proxy").toString();
			String mediaUrl = solution.get("mediaURL").toString();
			RecommendedObject retrievedObject = new RecommendedObject();
			retrievedObject.setName(title);
			retrievedObject.setUri(uri);
			retrievedObject.setMediaUrl(mediaUrl);
			retrievedObjects.add(retrievedObject);
		}

		return retrievedObjects;
	}

	public List<RecommendedObject> textQueryDbpedia(String dbpediaUri) {
		String ontology_service = "http://dbpedia.org/sparql";
		String endpoint = "otee:Endpoints";
		String endpointsSparql = "PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct  ?opera ?museum where { ?opera dbpedia-owl:museum ?museum. ?museum dbp-prop:location "
				+ "<" + dbpediaUri + ">" + "  } LIMIT 200";

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(
				ontology_service, String.format(endpointsSparql, endpoint));
		ResultSet results = queryExecution.execSelect();

		List<RecommendedObject> retrievedObjects = new ArrayList<RecommendedObject>();
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			String title = solution.get("opera").toString();
			String uri = solution.get("museum").toString();
//			String mediaUrl = solution.get("mediaURL").toString();
			RecommendedObject retrievedObject = new RecommendedObject();
			retrievedObject.setName(title);
			retrievedObject.setUri(uri);
//			retrievedObject.setMediaUrl(mediaUrl);
			retrievedObjects.add(retrievedObject);
		}

		return retrievedObjects;
	}
}
