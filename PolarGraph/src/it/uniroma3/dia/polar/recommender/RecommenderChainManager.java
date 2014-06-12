package it.uniroma3.dia.polar.recommender;

import it.uniroma3.dia.polar.graph.model.RecommendedObject;

import java.util.ArrayList;
import java.util.List;

/**
 * It is recommended to let a social recommender be the first in the chain
 * */
public class RecommenderChainManager {
	private List<Recommender> recommenderChain;
	
	public RecommenderChainManager(){
		this.recommenderChain = new ArrayList<>();
	}
	
	public void addRecommender(Recommender recommender){
		this.recommenderChain.add(recommender);
	}
	
	public List<RecommendedObject> startRecommendationChain(String userId){
		List<RecommendedObject> recommendedObjects = new ArrayList();
		for(Recommender recommender : recommenderChain){
			recommendedObjects = recommender.recommendObject(userId, recommendedObjects);
		}
		return recommendedObjects;
	}
}
