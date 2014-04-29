package it.uniroma3.dia.dependencyinjection;

import it.uniroma3.dia.polar.ranker.NaiveSocialRanker;
import it.uniroma3.dia.polar.ranker.Ranker;
import it.uniroma3.dia.polar.ranker.SemanticBaseRanker;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * The provider for Ranker implementation used by guice injection
 * */
public class RankerProvider implements Provider<Ranker> {

	private final RankerType rankerType;
	private final NaiveSocialRanker naiveRanker;
	private final SemanticBaseRanker semanticBaseRanker;

	@Inject
	public RankerProvider(final RankerType rankerParameter,final NaiveSocialRanker naiveRanker,
			final SemanticBaseRanker semanticBaseRanker) {
		this.naiveRanker = naiveRanker;
		this.semanticBaseRanker = semanticBaseRanker;
		this.rankerType = rankerParameter;
	}

	@Override
	public Ranker get() {
		switch(rankerType){
		case SEMANTICBASE:
			return semanticBaseRanker;
		default:
			return naiveRanker;
		}
	}

}
