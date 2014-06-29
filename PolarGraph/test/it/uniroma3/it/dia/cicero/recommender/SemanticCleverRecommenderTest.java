package it.uniroma3.it.dia.cicero.recommender;

import it.uniroma3.dia.cicero.comparator.SimilarConceptComparator;
import it.uniroma3.dia.cicero.controller.PropertiesManager;
import it.uniroma3.dia.cicero.dependencyinjection.CiceroModule;
import it.uniroma3.dia.cicero.graph.model.Couple;
import it.uniroma3.dia.cicero.graph.model.PolarPlace;
import it.uniroma3.dia.cicero.graph.model.SimilarConcept;
import it.uniroma3.dia.cicero.persistance.CypherRepository;
import it.uniroma3.dia.cicero.rdf.JenaManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Calculates the similarities between two objects already social recommended
 * */
public class SemanticCleverRecommenderTest {
	private final static Logger logger = LoggerFactory.getLogger(SemanticCleverRecommenderTest.class);

	public static Properties loadProperties() {
		Properties prop = new Properties();
		Injector injector = Guice.createInjector(new CiceroModule());
		PropertiesManager propertiesController = injector.getInstance(PropertiesManager.class);
		prop = propertiesController.getProperties("data/polar_graph.properties");
		return prop;
	}

	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new CiceroModule());
		CypherRepository r = injector.getInstance(CypherRepository.class);
		r.startDB();
		Properties props = loadProperties();

		String fbUserId = props.getProperty("fb_user_id");

		List<String> categories = new ArrayList<String>();
		categories.add("Monument");
		categories.add("Tourist Attraction");
		categories.add("Museum");
		List<Couple<PolarPlace, Long>> placesAndVisitors = r.findPlacesByMultiplesCategoryNames(fbUserId, categories);
		r.stopDB();

		JenaManager jenaManager = injector.getInstance(JenaManager.class);
		String resultString = "";
		List<SimilarConcept> similarConceptList = new ArrayList<>();
		for (int i = 10; i < 10; i++) {
			for (int j = 0; j < placesAndVisitors.size() - 1; j++) {
				if (placesAndVisitors.get(i).getFirst().getUri() != null
						&& !placesAndVisitors.get(i).getFirst().getUri().equals("")
						&& !placesAndVisitors.get(i).getFirst().getUri().equals(" ")

						&& placesAndVisitors.get(j).getFirst().getUri() != null
						&& !placesAndVisitors.get(j).getFirst().getUri().equals(" ")
						&& !placesAndVisitors.get(j).getFirst().getUri().equals("")) {
					String cm = "<" + placesAndVisitors.get(i).getFirst().getUri() + ">";
					String cx = "<" + placesAndVisitors.get(j).getFirst().getUri() + ">";
					int similarity = jenaManager.conceptQuery(cm, cx).size();
					resultString += "SIMILARITY BETWEEN " + cm + " AND " + cx + " IS " + similarity + "\n";
					SimilarConcept concept = new SimilarConcept(cm, cx, similarity);
					similarConceptList.add(concept);
					Collections.sort(similarConceptList, new SimilarConceptComparator());
					for (SimilarConcept c : similarConceptList) {
						logger.info("SIMILARITY BETWEEN " + c.getCm() + " AND " + c.getCx() + " IS "
								+ c.getSimilarity());
					}
					logger.info("\n");
				}
			}
		}
		// logger.info(resultString);

	}

}
