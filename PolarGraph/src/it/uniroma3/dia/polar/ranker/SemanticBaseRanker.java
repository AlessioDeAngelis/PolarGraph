package it.uniroma3.dia.polar.ranker;

import it.uniroma3.dia.polar.graph.model.RecommendedObject;
import it.uniroma3.dia.polar.persistance.CypherRepository;
import it.uniroma3.dia.polar.rdf.JenaManager;

import java.util.List;

import com.google.inject.Inject;


/**
 * It uses semantic information from linked open data, in particular europeana
 * */
public class SemanticBaseRanker implements Ranker{

	private final NaiveRanker naiveRanker;
	private final CypherRepository repository;
	private final JenaManager jenaManager;
	
	@Inject
	public SemanticBaseRanker(final CypherRepository repository, final NaiveRanker naiveRanker, final JenaManager jenaManager){
		this.repository = repository;
		this.naiveRanker = naiveRanker;
		this.jenaManager = jenaManager;
	}
	
	/**
	 * The places where to start are the same as the naive ranker.
	 * It extracts the title and it queries europeana to have more infos 
	 **/
	@Override
	public List<RecommendedObject> recommendObject(String userId) {
		List<RecommendedObject> rankedPlaces = naiveRanker.recommendObject(userId);
		List<RecommendedObject> recommendedObjects;
		String term = rankedPlaces.get(0).getName();
		recommendedObjects = jenaManager.textQueryEuropeana(term);
		return recommendedObjects;
	}

}
