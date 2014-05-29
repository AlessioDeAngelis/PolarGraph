package it.uniroma3.dia.polar.recommender.semantic;

import it.uniroma3.dia.polar.graph.model.RecommendedObject;
import it.uniroma3.dia.polar.persistance.CypherRepository;
import it.uniroma3.dia.polar.rdf.JenaManager;
import it.uniroma3.dia.polar.recommender.Recommender;
import it.uniroma3.dia.polar.recommender.social.SelectedCategoriesSocialRecommender;

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
	public List<RecommendedObject> recommendObject(String userId) {
		socialRecommender.setCategories(this.getCategories());
		List<RecommendedObject> rankedPlaces = socialRecommender.recommendObject(userId);
		List<RecommendedObject> recommendedObjects = new ArrayList<RecommendedObject>();
		String placeUri = " ";
		int i = 0;
		List<String> closeDbpediaConcepts = new ArrayList<>();
		if (rankedPlaces == null || rankedPlaces.size() <= 0) {
			recommendedObjects = rankedPlaces;
		} else {
			for (i = 0; i < rankedPlaces.size(); i++) {
				placeUri = rankedPlaces.get(i).getUri();

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
							obj.setName(concept.replaceFirst("<http://dbpedia.org/resource/", "").replace(">", ""));
							obj.setMediaUrl(mediaUrl);
							obj.setProvider("DBPEDIA");
							obj.setExternalLink(externalLink);
							recommendedObjects.add(obj);
						}
						break;
					}
				}
			}

		}

		rankedPlaces.clear(); // to free space
		return recommendedObjects;
	}
}
