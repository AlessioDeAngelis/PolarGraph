package it.uniroma3.dia.polar.disambiguator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.xml.sax.InputSource;

public class XMLParser {

	public Element getRootNode(String xmlText) {
		Element rootNode = null;

		try {
			SAXBuilder builder = new SAXBuilder();
			// InputStream inputStream = new
			// ByteArrayInputStream(xmlText.getBytes());
			// BufferedReader bufferedReader = new BufferedReader(new
			// InputStreamReader(inputStream));
			InputSource inputSource = new InputSource();
			inputSource.setCharacterStream(new StringReader(xmlText));
			Document doc = (Document) builder.build(inputSource);
			rootNode = doc.getRootElement();
		} catch (IOException io) {
			io.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		}

		return rootNode;
	}

	public List<SpottedPlace> parseGeonamesRDF(String xmlText) {
		xmlText = xmlText.replace("\"", "'");
		Element rootNode = getRootNode(xmlText);
		List<SpottedPlace> places = new ArrayList<SpottedPlace>();
//		XPathFactory xpfac = XPathFactory.instance();
//		XPathExpression xp = xpfac.compile("/RDF/Feature/name", Filters.element());
//		List<Element> elements = xp.evaluate(rootNode);
		// you cannot use xpath in this context because there is no declaration
		// of rdf prefix in the xml test
		if (rootNode != null && rootNode.getChildren() != null) {

			for (Element gnFeatureElement : rootNode.getChildren()) { // gn:Feature
				String name = gnFeatureElement.getChildText("name",
						Namespace.getNamespace("http://www.geonames.org/ontology#"));
				Element seeAlsoElement = gnFeatureElement.getChild("seeAlso",
						Namespace.getNamespace("http://www.w3.org/2000/01/rdf-schema#"));
				String uri = "";
				if (seeAlsoElement != null) {
					uri = seeAlsoElement.getAttributeValue("resource",
							Namespace.getNamespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#"));// rdfs:seeAlso[@rdf:resource]
				}
				String langitude = gnFeatureElement.getChildText("lat",
						Namespace.getNamespace("http://www.w3.org/2003/01/geo/wgs84_pos#"));// wgs84_pos:lat
				String longitude = gnFeatureElement.getChildText("long",
						Namespace.getNamespace("http://www.w3.org/2003/01/geo/wgs84_pos#"));// wgs84_pos:long
				SpottedPlace place = new SpottedPlace(name, uri, langitude, longitude);
				places.add(place);
			}
		}

		return places;
	}

	public List<SpottedPlace> parseSpotlight(String xmlText) {
		Element rootNode = getRootNode(xmlText);
		List<SpottedPlace> places = new ArrayList<SpottedPlace>();
		XPathFactory xpfac = XPathFactory.instance();
		XPathExpression xp = xpfac.compile("//Resource", Filters.element());
		List<Element> elements = xp.evaluate(rootNode);

		for (Element element : elements) {
			// dbpedia spotlight doesn't return lat & long
			String name = element.getAttributeValue("surfaceForm");
			String uri = element.getAttributeValue("URI");
			SpottedPlace place = new SpottedPlace(name, uri, "", "");
			places.add(place);
		}
		return places;
	}

}
