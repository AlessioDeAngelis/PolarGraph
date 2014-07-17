package it.uniroma3.dia.cicero.recommender.social;

import it.uniroma3.dia.cicero.comparator.RecommendedObjectComparatorByScoreDesc;
import it.uniroma3.dia.cicero.graph.model.Couple;
import it.uniroma3.dia.cicero.graph.model.PolarPlace;
import it.uniroma3.dia.cicero.graph.model.RecommendedObject;
import it.uniroma3.dia.cicero.persistance.CypherRepository;
import it.uniroma3.dia.cicero.rdf.JenaManager;
import it.uniroma3.dia.cicero.recommender.Recommender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

/**
 * First extracts all the places visited by friends and under a certain
 * Category. It gives a score according to the number of friends that visited
 * it.
 * */
public class RandomSocialRecommender extends
		AbstractSelectedCategoriesSocialRecommender {

	private CypherRepository repository;
	private final JenaManager jenaManager;
	@Inject
	public RandomSocialRecommender(CypherRepository repository, final JenaManager jenaManager) {
		super(repository);
		this.jenaManager = jenaManager;
	}

	@Override
	public List<RecommendedObject> recommendObject(String userId,
			List<RecommendedObject> inputObjects) {
		this.getRepository().startDB();
		List<RecommendedObject> outputObjects = new ArrayList<RecommendedObject>();

		List<PolarPlace> placesAndVisitorsByTouristAttractionCategory = this
				.getRepository().findAllPlaces();
		this.getRepository().stopDB();

		for (PolarPlace couple : placesAndVisitorsByTouristAttractionCategory) {
			if (couple.getUri() != null && !couple.getUri().equals("")
					&& !couple.getUri().equals(" ")) {
				RecommendedObject rankedPlace = convertToRecommendedObject(couple);
//				Map<String,String> extraInfo = jenaManager.queryDbpediaForExtraInfo("<"+couple.getUri()+">");
//				String mediaUrl = extraInfo.get("mediaUrl");
//				rankedPlace.setMediaUrl(mediaUrl);
				rankedPlace.setScore(1);
				outputObjects.add(rankedPlace);
			}
		}
		if (outputObjects != null && outputObjects.size() > 0) {
			Collections.shuffle(outputObjects);
		}

		if (outputObjects.size() > 10) {
			outputObjects = outputObjects.subList(0, 10);
		}
		for(RecommendedObject rec : outputObjects){
			Map<String,String> extraInfo = jenaManager.queryDbpediaForExtraInfo("<"+rec.getUri()+">");
			String mediaUrl = extraInfo.get("mediaUrl");
			rec.setMediaUrl(mediaUrl);
		}
		return outputObjects;
	}
	
}
