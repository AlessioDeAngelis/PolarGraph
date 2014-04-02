package it.uniroma3.dia.polar.ranker;

import java.util.List;

public interface Ranker {

	public List<RankedPlace> rankPlaces(String userId);
}
