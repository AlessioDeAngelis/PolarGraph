package it.uniroma3.dia.cicero.dependencyinjection;

import it.uniroma3.dia.cicero.recommender.Recommender;
import it.uniroma3.dia.cicero.recommender.semantic.SemanticBaseRecommender;
import it.uniroma3.dia.cicero.recommender.social.NaiveSocialRecommender;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * The provider for Ranker implementation used by guice injection
 * */
public class RankerProvider implements Provider<Recommender> {

	private final RankerType rankerType;
	private final NaiveSocialRecommender naiveRanker;
	private final SemanticBaseRecommender semanticBaseRanker;

	@Inject
	public RankerProvider(final RankerType rankerParameter,final NaiveSocialRecommender naiveRanker,
			final SemanticBaseRecommender semanticBaseRanker) {
		this.naiveRanker = naiveRanker;
		this.semanticBaseRanker = semanticBaseRanker;
		this.rankerType = rankerParameter;
	}

	@Override
	public Recommender get() {
		switch(rankerType){
		case SEMANTICBASE:
			return semanticBaseRanker;
		default:
			return naiveRanker;
		}
	}

}
