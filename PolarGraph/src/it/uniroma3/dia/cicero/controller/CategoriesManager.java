package it.uniroma3.dia.cicero.controller;

import it.uniroma3.dia.cicero.comparator.CoupleCategoryScoreComparatorByScoreDesc;
import it.uniroma3.dia.cicero.graph.model.Category;
import it.uniroma3.dia.cicero.graph.model.Couple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class CategoriesManager {

	private List<String> culturalHeritageCategories;

	public CategoriesManager() {
		this.culturalHeritageCategories = new ArrayList<String>();
		initCulturalHeritageCategories();
	}

	private void initCulturalHeritageCategories() {
		this.culturalHeritageCategories.add("Monument");
		this.culturalHeritageCategories.add("Historical Place");
		this.culturalHeritageCategories.add("Arts & Entertainment");
		this.culturalHeritageCategories.add("Tourist Attraction");
		this.culturalHeritageCategories.add("Public Square");
		this.culturalHeritageCategories.add("Amusement");
		this.culturalHeritageCategories.add("Landmark");
		this.culturalHeritageCategories.add("Statue & Fountain");
		this.culturalHeritageCategories.add("Public Places & Attractions");
		this.culturalHeritageCategories.add("Museum");
		this.culturalHeritageCategories.add("Park");
		this.culturalHeritageCategories.add("Lake");
		this.culturalHeritageCategories.add("Mountain");
		this.culturalHeritageCategories.add("National Park");
		this.culturalHeritageCategories.add("Attractions/Things to Do");
		this.culturalHeritageCategories.add("Tourist Information");
		this.culturalHeritageCategories.add("Tour Guide");
		this.culturalHeritageCategories.add("Water Park");
		this.culturalHeritageCategories.add("Outdoor Recreation");
		this.culturalHeritageCategories.add("Theatre");
		this.culturalHeritageCategories.add("Tours & Sightseeing");
		this.culturalHeritageCategories.add("Auditorium");
		this.culturalHeritageCategories.add("Zoo & Aquarium");
		this.culturalHeritageCategories.add("State Park");
		this.culturalHeritageCategories.add("History Museum");
		this.culturalHeritageCategories.add("Theme Park");
		this.culturalHeritageCategories.add("Catholic Church");
		this.culturalHeritageCategories.add("Bridge");
		this.culturalHeritageCategories.add("Island");
		this.culturalHeritageCategories.add("Cabin");
		this.culturalHeritageCategories.add("Church");
		this.culturalHeritageCategories.add("Cruise");
		this.culturalHeritageCategories.add("Circus");
		this.culturalHeritageCategories.add("Art Gallery");
		this.culturalHeritageCategories.add("Wildlife Sanctuary");
		this.culturalHeritageCategories.add("Cruise Excursions");
		this.culturalHeritageCategories.add("Buddhist Temple");
		this.culturalHeritageCategories.add("River");
		this.culturalHeritageCategories.add("Environmental Conservation");
		this.culturalHeritageCategories.add("Modern Art Museum");
		this.culturalHeritageCategories.add("Public Places");
		this.culturalHeritageCategories.add("City");
	}

	public List<Category> calculateUserFavouriteCategories(List<Couple<Category, Double>> retrievedCategories, int topK) {
		List<Category> favouriteCategories = new ArrayList<>();
		double culturalWeight = 0.85; // the weight for the cultural categories
		List<Double> scores = new ArrayList<>();
		/* Calculate max score for normalization */
		for (Couple<Category, Double> retrievedCategory : retrievedCategories) {
			scores.add(retrievedCategory.getSecond());
		}
		double maxScore = Collections.max(scores);
		/* Normalize and weight the score*/
		for (Couple<Category, Double> retrievedCategory : retrievedCategories) {
			double oldScore = retrievedCategory.getSecond();
			double normalizedScore = oldScore / maxScore;
			double boostedNormalizedScore = normalizedScore;
			if (isCulturalHeritageCategory(retrievedCategory.getFirst())) {
				boostedNormalizedScore *= culturalWeight;
			} else {
				boostedNormalizedScore *= (1 - culturalWeight);
			}
			retrievedCategory.setSecond(boostedNormalizedScore);
			System.out.println("CATEGORY: " + retrievedCategory.getFirst().getName() + " oldScore " + oldScore + " normalized " + normalizedScore +" boosted " + boostedNormalizedScore);
		}
		Collections.sort(retrievedCategories, new CoupleCategoryScoreComparatorByScoreDesc());
		
		/* Take only the top K*/
		for(int i = 0; i < retrievedCategories.size() && i < topK; i++){
			favouriteCategories.add(retrievedCategories.get(i).getFirst());			
		}
		for(Category c : favouriteCategories){
			System.out.println("FAV: " + c.getName());
		}
		return favouriteCategories;
	}

	private boolean isCulturalHeritageCategory(Category category) {
		return category != null && this.culturalHeritageCategories.contains(category.getName());
	}
}
