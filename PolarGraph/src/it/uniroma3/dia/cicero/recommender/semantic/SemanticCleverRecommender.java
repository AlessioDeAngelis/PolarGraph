package it.uniroma3.dia.cicero.recommender.semantic;

import it.uniroma3.dia.cicero.comparator.SimilarConceptComparator;
import it.uniroma3.dia.cicero.graph.model.RecommendedObject;
import it.uniroma3.dia.cicero.graph.model.SimilarConcept;
import it.uniroma3.dia.cicero.persistance.CypherRepository;
import it.uniroma3.dia.cicero.rdf.JenaManager;
import it.uniroma3.dia.cicero.recommender.Recommender;
import it.uniroma3.dia.cicero.recommender.social.SelectedCategoriesSocialRecommender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * It uses semantic information from linked open data, in particular dbpedia
 * */
public class SemanticCleverRecommender extends Recommender {
	private final static Logger logger = LoggerFactory.getLogger(SemanticCleverRecommender.class);

	private final SelectedCategoriesSocialRecommender socialRanker;
	private final CypherRepository repository;
	private final JenaManager jenaManager;

	@Inject
	public SemanticCleverRecommender(final CypherRepository repository,
			final SelectedCategoriesSocialRecommender socialRanker, final JenaManager jenaManager) {
		this.repository = repository;
		this.socialRanker = socialRanker;
		this.jenaManager = jenaManager;
	}

	// TODO: THIS METHOD NEEDS A REFACTORING, ESPECIALLY TO INCREASE
	// PERFORMANCES

	/**
	 * The places where to start are the same as the naive ranker. It extracts
	 * the title and it queries dbpedia to have more infos
	 **/
	@Override
	public List<RecommendedObject> recommendObject(String userId, List<RecommendedObject> inputObjects) {
//		List<RecommendedObject> inputObjects = socialRanker.recommendObject(userId);
		List<RecommendedObject> recommendedObjects = new ArrayList<RecommendedObject>();

		String resultString = "";
		List<SimilarConcept> similarConceptList = new ArrayList<>();
		for (int i = 0; i < 1; i++) {
			for (int j = 0; j < inputObjects.size() - 1; j++) {
				if (i != j && inputObjects.get(i).getUri() != null && !inputObjects.get(i).getUri().equals("")
						&& !inputObjects.get(i).getUri().equals(" ")

						&& inputObjects.get(j).getUri() != null && !inputObjects.get(j).getUri().equals(" ")
						&& !inputObjects.get(j).getUri().equals("")

				) {
					double alphaFactor = inputObjects.get(i).getScore() * 0.6 + inputObjects.get(j).getScore() * 0.4;
					String cm = "<" + inputObjects.get(i).getUri() + ">";
					String cx = "<" + inputObjects.get(j).getUri() + ">";
					int similarity = jenaManager.conceptQuery(cm, cx).size();
					resultString += "SIMILARITY BETWEEN " + cm + " AND " + cx + " IS " + similarity
							+ " while with the factor is " + similarity * alphaFactor + "\n";
					SimilarConcept concept = new SimilarConcept(cm, cx, similarity);
					similarConceptList.add(concept);
					Collections.sort(similarConceptList, new SimilarConceptComparator());
					
					// logger.info("\n");

				}
			}
		}
		
		for (SimilarConcept c : similarConceptList) {
			logger.info("SIMILARITY BETWEEN " + c.getCm() + " AND " + c.getCx() + " IS "
					+ c.getSimilarity() + " while with the factor is " + c.getSimilarity());
			RecommendedObject recommendedObject = new RecommendedObject();
			recommendedObject.setUri(c.getCx());
			String name = c.getCx().replaceFirst("<http://dbpedia.org/resource/", "").replace(">", "");

			recommendedObject.setName(name); // TODO: updated it
												// with the label
												// and not the uri
			recommendedObject.setWhy(c.getCm());
			recommendedObject.setScore(c.getSimilarity());
//			String imageUrl = jenaManager.queryDbpediaForImageUrl(c.getCx());
			Map<String,String> extraInfo = jenaManager.queryDbpediaForExtraInfo(c.getCx());
			String imageUrl = extraInfo.get("mediaUrl");
			String externalLink = extraInfo.get("externalLink");
			recommendedObject.setMediaUrl(imageUrl);
			recommendedObject.setExternalLink(externalLink);

			recommendedObjects.add(recommendedObject);
			// TO BE EXTENDED, EVENTUALLY
		}
		// // query for the images
		// logger.info("QUERYING FOR IMAGES ");
		// for (int i = 0; i < recommendedObjects.size(); i++) {
		// if (i < 10) {
		// RecommendedObject obj = recommendedObjects.get(i);
		// String imageUrl = jenaManager.queryDbpediaForImageUrl(obj.getUri());
		// obj.setMediaUrl(imageUrl);
		// String name =
		// obj.getUri().replaceFirst("<http://dbpedia.org/resource/",
		// "").replace(">", "");
		// obj.setName(name);
		// }
		// }

		inputObjects.clear(); // to free space
		return recommendedObjects;
	}
	
	/**
	 * The places where to start are the same as the naive ranker. It extracts
	 * the title and it queries dbpedia to have more infos
	 **/
//	@Override
	public List<RecommendedObject> recommendObjectBackup(String userId, List<RecommendedObject> inputObjects) {
//		List<RecommendedObject> inputObjects = socialRanker.recommendObject(userId);
		List<RecommendedObject> recommendedObjects = new ArrayList<RecommendedObject>();

		String resultString = "";
		List<SimilarConcept> similarConceptList = new ArrayList<>();
		for (int i = 0; i < 1; i++) {
			for (int j = 0; j < inputObjects.size() - 1; j++) {
				if (i != j && inputObjects.get(i).getUri() != null && !inputObjects.get(i).getUri().equals("")
						&& !inputObjects.get(i).getUri().equals(" ")

						&& inputObjects.get(j).getUri() != null && !inputObjects.get(j).getUri().equals(" ")
						&& !inputObjects.get(j).getUri().equals("")

				) {
					double alphaFactor = inputObjects.get(i).getScore() * 0.6 + inputObjects.get(j).getScore() * 0.4;
					String cm = "<" + inputObjects.get(i).getUri() + ">";
					String cx = "<" + inputObjects.get(j).getUri() + ">";
					int similarity = jenaManager.conceptQuery(cm, cx).size();
					resultString += "SIMILARITY BETWEEN " + cm + " AND " + cx + " IS " + similarity
							+ " while with the factor is " + similarity * alphaFactor + "\n";
					SimilarConcept concept = new SimilarConcept(cm, cx, similarity);
					similarConceptList.add(concept);
					Collections.sort(similarConceptList, new SimilarConceptComparator());
					for (SimilarConcept c : similarConceptList) {
						logger.info("SIMILARITY BETWEEN " + c.getCm() + " AND " + c.getCx() + " IS "
								+ c.getSimilarity() + " while with the factor is " + c.getSimilarity() * alphaFactor);
						RecommendedObject recommendedObject = new RecommendedObject();
						recommendedObject.setUri(c.getCx());
						String name = c.getCx().replaceFirst("<http://dbpedia.org/resource/", "").replace(">", "");

						recommendedObject.setName(name); // TODO: updated it
															// with the label
															// and not the uri
						recommendedObject.setWhy(c.getCm());
						recommendedObject.setScore(c.getSimilarity() * alphaFactor);
						String imageUrl = jenaManager.queryDbpediaForImageUrl(c.getCx());
						recommendedObject.setMediaUrl(imageUrl);

						recommendedObjects.add(recommendedObject);
						// TO BE EXTENDED, EVENTUALLY
					}
					// logger.info("\n");

				}
			}
		}
		// // query for the images
		// logger.info("QUERYING FOR IMAGES ");
		// for (int i = 0; i < recommendedObjects.size(); i++) {
		// if (i < 10) {
		// RecommendedObject obj = recommendedObjects.get(i);
		// String imageUrl = jenaManager.queryDbpediaForImageUrl(obj.getUri());
		// obj.setMediaUrl(imageUrl);
		// String name =
		// obj.getUri().replaceFirst("<http://dbpedia.org/resource/",
		// "").replace(">", "");
		// obj.setName(name);
		// }
		// }

		inputObjects.clear(); // to free space
		return recommendedObjects;
	}
}
