package it.uniroma3.dia.dependencyinjection;

import it.uniroma3.dia.polar.ranker.NaiveRanker;
import it.uniroma3.dia.polar.ranker.Ranker;
import it.uniroma3.dia.polar.ranker.SemanticBaseRanker;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * The provider for Ranker implementation used by guice injection
 * */
public class RankerProvider implements Provider<Ranker> {

	private final RankerType rankerType;
	private final NaiveRanker naiveRanker;
	private final SemanticBaseRanker semanticBaseRanker;

	@Inject
	public RankerProvider(final RankerType rankerParameter,final NaiveRanker naiveRanker,
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
