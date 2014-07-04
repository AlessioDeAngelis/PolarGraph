package it.uniroma3.dia.cicero.recommender.semantic;

import it.uniroma3.dia.cicero.graph.model.RecommendedObject;
import it.uniroma3.dia.cicero.persistance.CypherRepository;
import it.uniroma3.dia.cicero.rdf.JenaManager;
import it.uniroma3.dia.cicero.recommender.Recommender;
import it.uniroma3.dia.cicero.recommender.social.SelectedCategoriesSocialRecommender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * It recommends places according to the closeness
 * */
public class SemanticCloserPlacesRecommender extends Recommender {
	private final static Logger logger = LoggerFactory.getLogger(SemanticCloserPlacesRecommender.class);

	private final SelectedCategoriesSocialRecommender socialRecommender;
	private final CypherRepository repository;
	private final JenaManager jenaManager;

	@Inject
	public SemanticCloserPlacesRecommender(final CypherRepository repository,
			final SelectedCategoriesSocialRecommender socialRecommender, final JenaManager jenaManager) {
		this.repository = repository;
		this.socialRecommender = socialRecommender;
		this.jenaManager = jenaManager;
	}

	/**
	 * The places where to start are the same as the naive ranker. It extracts
	 * the title and it queries dbpedia to have more infos
	 **/
	@Override
	public List<RecommendedObject> recommendObject(String userId, List<RecommendedObject> inputObjects) {
//		socialRecommender.setCategories(this.getCategories());
//		List<RecommendedObject> inputObjects = socialRecommender.recommendObject(userId,inputObjects);
		List<RecommendedObject> recommendedObjects = new ArrayList<RecommendedObject>();
		String placeUri = " ";
		int i = 0;
		int numberOfRecommendedDbPediaObjects = 0;
		List<String> closeDbpediaConcepts = new ArrayList<>();
		if (inputObjects == null || inputObjects.size() <= 0) {
			recommendedObjects = inputObjects;
		} else {
			for (i = 0; i < inputObjects.size(); i++) {
				placeUri = inputObjects.get(i).getUri();

				if (!placeUri.equals("") && !placeUri.equals(" ")) {
					// get the concepts
					closeDbpediaConcepts = jenaManager.findCloserPlacesFromDbpedia(placeUri);
					// extract extra infos and create a recommender object
					if (closeDbpediaConcepts != null && closeDbpediaConcepts.size() > 0) {
						for (String concept : closeDbpediaConcepts) {
							Map<String,String> extraAttributes2values = jenaManager.queryDbpediaForExtraInfo("<"+concept+">");
							String mediaUrl = extraAttributes2values.get("mediaUrl");
							String externalLink = extraAttributes2values.get("externalLink");
							RecommendedObject obj = new RecommendedObject();
							obj.setUri(concept);
							obj.setName(concept.replaceFirst("http://dbpedia.org/resource/", "").replaceAll("_", " "));
							obj.setMediaUrl(mediaUrl);
							obj.setProvider("DBPEDIA");
							obj.setExternalLink(externalLink);
							obj.setWhy(inputObjects.get(i).getName());
							recommendedObjects.add(obj);
							numberOfRecommendedDbPediaObjects ++;
						}
					}
				}
				//let's suggest at least 5 objects before thebreak
				if(numberOfRecommendedDbPediaObjects > 5){
					break;						

				}
			}

		}

		inputObjects.clear(); // to free space
		return recommendedObjects;
	}
}
