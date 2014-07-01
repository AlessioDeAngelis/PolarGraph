package it.uniroma3.it.dia.cicero.recommender;

import it.uniroma3.dia.cicero.controller.PropertiesManager;
import it.uniroma3.dia.cicero.dependencyinjection.CiceroModule;
import it.uniroma3.dia.cicero.graph.model.Couple;
import it.uniroma3.dia.cicero.graph.model.PolarPlace;
import it.uniroma3.dia.cicero.graph.model.SimilarConcept;
import it.uniroma3.dia.cicero.persistance.CypherRepository;
import it.uniroma3.dia.cicero.rdf.JenaManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Calculates the number of occurences of graph patterns
 * */
public class SemanticCleverRecommenderTest2 {
	private final static Logger logger = LoggerFactory.getLogger(SemanticCleverRecommenderTest2.class);

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
		Multiset<String> multiset = HashMultiset.create();
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
		for (int i = 0; i < 1; i++) {
			// for (int j = 0; j < placesAndVisitors.size() - 1; j++) {
			for (int j = 1; j < 2; j++) {

				if (i != j && placesAndVisitors.get(i).getFirst().getUri() != null
						&& !placesAndVisitors.get(i).getFirst().getUri().equals("")
						&& !placesAndVisitors.get(i).getFirst().getUri().equals(" ")

						&& placesAndVisitors.get(j).getFirst().getUri() != null
						&& !placesAndVisitors.get(j).getFirst().getUri().equals(" ")
						&& !placesAndVisitors.get(j).getFirst().getUri().equals("")) {
					String cm = "<" + placesAndVisitors.get(i).getFirst().getUri() + ">";
					 String cx = "?cx";
//					String cx = "<" + placesAndVisitors.get(j).getFirst().getUri() + ">";
					List<String> extractedConcepts = jenaManager.conceptQuery(cm, cx);

					for (String concept : extractedConcepts) {
						multiset.add(concept);
					}

				}
			}
			// }
			// logger.info(resultString);

			logger.info(placesAndVisitors.get(0).getFirst().getUri() + "\n ============ \n\n");

			Set<Entry<String>> entrySet = multiset.entrySet();
			List<Couple<String, Integer>> couples = new ArrayList<>();
			for (Entry<String> entry : entrySet) {
				Couple<String, Integer> couple = new Couple(entry.getElement(), entry.getCount());
				couples.add(couple);
			}
			Collections.sort(couples, new StringEntryComparator());
			for (Couple c : couples) {
				logger.info(c.getFirst() + ", " + c.getSecond());
			}
		}
	}

}

class StringEntryComparator implements Comparator<Couple<String, Integer>> {

	@Override
	public int compare(Couple<String, Integer> o1, Couple<String, Integer> o2) {
		int result = 0;
		return o2.getSecond() - o1.getSecond();
	}
}
