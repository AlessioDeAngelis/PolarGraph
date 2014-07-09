package it.uniroma3.dia.cicero.controller;

import it.uniroma3.dia.cicero.graph.model.RecommendedObject;
import it.uniroma3.dia.cicero.recommender.Recommender;

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
			for(int i = 0; i < 5 && i < recommendedObjects.size(); i++){
				System.out.println(recommendedObjects.get(i));
			}
		}
		return recommendedObjects;
	}
}
