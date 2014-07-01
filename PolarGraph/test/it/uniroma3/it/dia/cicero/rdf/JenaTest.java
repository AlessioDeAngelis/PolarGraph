package it.uniroma3.it.dia.cicero.rdf;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;


public class JenaTest {

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		textQuery();
		System.out.println("ENDED IN " + (System.currentTimeMillis() - start));
	}	

	
	public static void textQuery(){
		String ontology_service = "http://europeana.ontotext.com/sparql";
		String endpoint = "otee:Endpoints";
		String endpointsSparql = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX edm: <http://www.europeana.eu/schemas/edm/> PREFIX ore: <http://www.openarchives.org/ore/terms/> PREFIX dc: <http://purl.org/dc/elements/1.1/>  SELECT ?proxy ?title ?mediaURL ?creator ?source WHERE {     FILTER regex(?title, 'Buccin') ?resource ore:proxyIn ?proxy ; dc:title ?title ; dc:creator ?creator ; dc:source ?source . ?proxy edm:isShownBy ?mediaURL .  }";
//		endpointsSparql = " PREFIX edm: <http://www.europeana.eu/schemas/edm/> PREFIX ore: <http://www.openarchives.org/ore/terms/> PREFIX dc: <http://purl.org/dc/elements/1.1/>  SELECT ?title ?mediaURL ?creator ?source WHERE {     FILTER regex(?title, 'Gioconda') ?resource dc:title ?title ; dc:creator ?creator ; dc:source ?source .  }";
//		endpointsSparql = " PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX ore: <http://www.openarchives.org/ore/terms/> PREFIX dc: <http://purl.org/dc/elements/1.1/> SELECT distinct ?s ?subject ?title WHERE { ?s rdf:type ore:Proxy; dc:subject ?subject ; dc:title ?title.  FILTER ( regex ( str(?subject), 'Geiranger') ) } LIMIT 100";
//		endpointsSparql = " PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX ore: <http://www.openarchives.org/ore/terms/> PREFIX dc: <http://purl.org/dc/elements/1.1/> SELECT distinct ?s ?subject ?title WHERE { ?s dc:subject ?subject ; dc:title ?title.  FILTER ( regex ( str(?subject), 'Colosseo') ) } LIMIT 100";

		QueryExecution x = QueryExecutionFactory.sparqlService(ontology_service,
				String.format(endpointsSparql, endpoint));
		ResultSet results = x.execSelect();
		ResultSetFormatter.out(System.out, results);
	}
	
	public static void europeanaMusicProva() {
		String ontology_service = "http://europeana.ontotext.com/sparql";
		String endpoint = "otee:Endpoints";
		String endpointsSparql = "PREFIX dbpedia:<http://dbpedia.org/resource/> PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> select distinct ?y  where { SERVICE <http://dbpedia.org/sparql> {dbpedia:Rome rdf:type ?y} } LIMIT 1000";
		endpointsSparql = "PREFIX dc: <http://purl.org/dc/elements/1.1/> PREFIX edm: <http://www.europeana.eu/schemas/edm/> SELECT ?s ?p ?o ?title WHERE{ ?s a edm:ProvidedCHO. ?title dc:title ?s.} LIMIT 100";
		QueryExecution x = QueryExecutionFactory.sparqlService(ontology_service,
				String.format(endpointsSparql, endpoint));
		ResultSet results = x.execSelect();
		ResultSetFormatter.out(System.out, results);
	}

	public static void europeanaMusicOk() {
		String ontology_service = "http://europeana.ontotext.com/sparql";
		String endpoint = "otee:Endpoints";
		String endpointsSparql = "PREFIX edm: <http://www.europeana.eu/schemas/edm/> PREFIX ore: <http://www.openarchives.org/ore/terms/> PREFIX dc: <http://purl.org/dc/elements/1.1/> SELECT ?title ?mediaURL ?creator ?source WHERE { ?resource edm:type 'VIDEO' ; ore:proxyIn ?proxy ; dc:title ?title ;  dc:creator ?creator ;  dc:source ?source . ?proxy edm:isShownBy ?mediaURL .}	OFFSET 600 LIMIT 100 ";
		QueryExecution x = QueryExecutionFactory.sparqlService(ontology_service,
				String.format(endpointsSparql, endpoint));
		ResultSet results = x.execSelect();
		ResultSetFormatter.out(System.out, results);
	}

	public static void europeanaEndpoint() {
		String ontology_service = "http://europeana.ontotext.com/sparql";
		String endpoint = "otee:Endpoints";
		String endpointsSparql = "PREFIX edm: <http://www.europeana.eu/schemas/edm/> "
				+ "PREFIX ore: <http://www.openarchives.org/ore/terms/> "
				+

				" SELECT DISTINCT ?CHO ?year "
				+ " WHERE { ?EuropeanaObject  edm:year  ?year ;  edm:hasMet <http://sws.geonames.org/3017382/> . 	?EuropeanaObject ore:proxyFor ?CHO. FILTER (?year < '1800')  FILTER (?year > '1700')} ORDER BY asc (?year) LIMIT 25";

		QueryExecution x = QueryExecutionFactory.sparqlService(ontology_service,
				String.format(endpointsSparql, endpoint));
		ResultSet results = x.execSelect();
		ResultSetFormatter.out(System.out, results);
	}

	public void otee() {
		String ontology_service = "http://ambit.uni-plovdiv.bg:8080/ontology";
		String endpoint = "otee:Endpoints";
		String endpointsSparql = "PREFIX ot:<http://www.opentox.org/api/1.1#>\n"
				+ "	PREFIX ota:<http://www.opentox.org/algorithms.owl#>\n"
				+ "	PREFIX owl:<http://www.w3.org/2002/07/owl#>\n" + "	PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"
				+ "	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "	PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "	PREFIX otee:<http://www.opentox.org/echaEndpoints.owl#>\n" + "		select ?url ?title\n"
				+ "		where {\n" + "		?url rdfs:subClassOf %s.\n" + "		?url dc:title ?title.\n" + "		}\n";

		QueryExecution x = QueryExecutionFactory.sparqlService(ontology_service,
				String.format(endpointsSparql, endpoint));
		ResultSet results = x.execSelect();
		ResultSetFormatter.out(System.out, results);
	}
}
