package it.uniroma3.dia.polar.ranker;

import it.uniroma3.dia.polar.graph.model.RecommendedObject;

import java.util.List;

public interface Ranker {

	public List<RecommendedObject> recommendObject(String userId);
}