package it.uniroma3.dia.polar.rdf;

import it.uniroma3.dia.polar.graph.model.RecommendedObject;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.sparql.mgt.Explain;

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

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(ontology_service,
				String.format(endpointsSparql, endpoint));
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

	public static List<RecommendedObject> conceptQuery(String cm, String cx) {
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
		 
		 String heritageTypesQueryString = " {" +" ?c a <" +heritageTypes.get(0)+">.} ";
		 for(int i = 1; i< heritageTypes.size(); i++){
			 heritageTypesQueryString += " UNION {" +" ?c a <" +heritageTypes.get(i)+">.} ";
		 }
		
		
		String ontology_service = "http://dbpedia.org/sparql";
//		ontology_service = "http://live.dbpedia.org/sparql";
		String endpoint = "otee:Endpoints";
		String query1 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 ?c. " + cx + " ?p2 ?c. ?c rdf:type dbpedia-owl:Place.} LIMIT 50 ";
		String query2 = "PREFIX  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 " + cx + ". ?c ?p2 " + cx + ". ?c rdf:type dbpedia-owl:Place.} LIMIT 50 ";
		String query3 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 " + cx + ". " + cx + " ?p2 ?c. ?c rdf:type dbpedia-owl:Place.} LIMIT 50 ";
		String query4 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cx + " ?p1 " + cm + ". " + cm + " ?p2 ?c. ?c rdf:type dbpedia-owl:Place.} LIMIT 50 ";
		String query5 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 ?c. ?c ?p2 ?cy. ?cy ?p3 " + cx + ". ?c rdf:type dbpedia-owl:Place.} LIMIT 50 ";
		
		String query6 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 ?c. " + cx + " ?p2 ?c.} LIMIT 50 ";
		String query7 = "PREFIX  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 " + cx + ". ?c ?p2 " + cx + ". } LIMIT 50 ";
		String query8 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 " + cx + ". " + cx + " ?p2 ?c.} LIMIT 50 ";
		String query9 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cx + " ?p1 " + cm + ". " + cm + " ?p2 ?c. } LIMIT 50 ";
		String query10 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 ?c. ?c ?p2 ?cy. ?cy ?p3 " + cx + ". } LIMIT 50 ";
		
		

		String query11 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 ?c. " + cx + " ?p2 ?c. " + heritageTypesQueryString+ " } LIMIT 50 ";
		String query12 = "PREFIX  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 " + cx + ". ?c ?p2 " + cx + ". " + heritageTypesQueryString+ " } LIMIT 50 ";
		String query13 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 " + cx + ". " + cx + " ?p2 ?c. " + heritageTypesQueryString+ " } LIMIT 50 ";
		String query14 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cx + " ?p1 " + cm + ". " + cm + " ?p2 ?c. } LIMIT 50 ";
		String query15 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 ?c. ?c ?p2 ?cy. ?cy ?p3 " + cx + ". " + heritageTypesQueryString+ " } LIMIT 50 ";
		String query16 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX dbp-prop: <http://dbpedia.org/property/> PREFIX dbpedia: <http://dbpedia.org/resource/> PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> select distinct ?c WHERE{ "
				+ cm + " ?p1 ?cy. ?cy ?p2 ?c. "+ cx +" ?p3 ?c. " + heritageTypesQueryString+ " } LIMIT 50 ";

		List<String> allQueries = new ArrayList<>();
		allQueries.add(query1);
		allQueries.add(query2);
		allQueries.add(query3);
		allQueries.add(query4);
		allQueries.add(query5);
		allQueries.add(query6);
		allQueries.add(query7);
		allQueries.add(query8);
		allQueries.add(query9);
		allQueries.add(query10);
		allQueries.add(query11);
		allQueries.add(query12);
		allQueries.add(query13);
		allQueries.add(query14);
		allQueries.add(query15);
		allQueries.add(query16);
		


		StringBuilder finalResult = new StringBuilder();
		QueryExecution queryExecution = null;
		int i = 1;
		for (String query : allQueries) {
		
			finalResult.append("QUERY " +i +" : " + query + "\n");
			finalResult.append("==================");
			queryExecution = QueryExecutionFactory.sparqlService(ontology_service, String.format(query, endpoint));
			ResultSet results = queryExecution.execSelect();

			while (results.hasNext()) {
				QuerySolution solution = results.next();
				String concept = solution.get("c").toString();
				finalResult.append("\n---------------- \n" + concept);
			}
			finalResult.append("\n\n");
			i++;
		}

		System.out.println(finalResult.toString());
		return null;
	}

	public static void main(String[] args) {
		String cm = "dbpedia:Girl_with_a_Pearl_Earring";
//		String cx = "dbpedia:Johannes_Vermeer";
		String cx = "?cx";
		conceptQuery(cm, cx);
	}
}
