package it.uniroma3.it.dia.cicero.disambiguator;

import org.junit.Test;

import it.uniroma3.dia.cicero.parser.XMLParser;

public class XMLParserTest {
	public void parseSpotlightTest() {

	}

	@Test
	public void getRootNodeTest() {
		String xmlText = "<?xml version='1.0'?> <student xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><firstName>Luca</firstName></student>";
		xmlText = "<?xml version='1.0' encoding='utf-8'?><Annotation text='Moholt Trondheim' confidence='0.1' support='10' types='' sparql='' policy='whitelist'><Resources><Resource URI='http://dbpedia.org/resource/Moholt' support='9' types='' surfaceForm='Moholt' offset='0' similarityScore='0.999999994833253' percentageOfSecondRank='0.0'/><Resource URI='http://dbpedia.org/resource/Trondheim' support='3968' types='Schema:Place,DBpedia:Place,DBpedia:PopulatedPlace,Schema:AdministrativeArea,DBpedia:AdministrativeRegion' surfaceForm='Trondheim' offset='7' similarityScore='0.9999909445007867' percentageOfSecondRank='1.9042330166124197E-6'/></Resources></Annotation>";
		xmlText = "<?xml version='1.0' encoding='UTF-8' standalone='no'?><rdf:RDF xmlns:cc='http://creativecommons.org/ns#' xmlns:dcterms='http://purl.org/dc/terms/' xmlns:foaf='http://xmlns.com/foaf/0.1/' xmlns:gn='http://www.geonames.org/ontology#' xmlns:owl='http://www.w3.org/2002/07/owl#' xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' xmlns:rdfs='http://www.w3.org/2000/01/rdf-schema#' xmlns:wgs84_pos='http://www.w3.org/2003/01/geo/wgs84_pos#'/>";
		xmlText = "<?xml version='1.0' encoding='UTF-8' standalone='no'?><rdf:RDF xmlns:cc='http://creativecommons.org/ns#' xmlns:dcterms='http://purl.org/dc/terms/' xmlns:foaf='http://xmlns.com/foaf/0.1/' xmlns:gn='http://www.geonames.org/ontology#' xmlns:owl='http://www.w3.org/2002/07/owl#' xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' xmlns:rdfs='http://www.w3.org/2000/01/rdf-schema#' xmlns:wgs84_pos='http://www.w3.org/2003/01/geo/wgs84_pos#'><gn:Feature rdf:about='http://sws.geonames.org/3145625/'><rdfs:isDefinedBy rdf:resource='http://sws.geonames.org/3145625/about.rdf'/><gn:name>Moholt</gn:name>		<wgs84_pos:lat>63.4</wgs84_pos:lat><wgs84_pos:long>10.43333</wgs84_pos:long><rdfs:seeAlso rdf:resource='http://dbpedia.org/resource/Moholt'/></gn:Feature></rdf:RDF>";
		XMLParser parser = new XMLParser();
		System.out.println(parser.getRootNode(xmlText));
	}
}
