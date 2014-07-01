package it.uniroma3.it.dia.cicero.rdf;

public class CreateCategoryBox {
	public static void main(String[] args) {
		String categories[] = { "Monument", "Historical Place", "Arts & Entertainment", "Tourist Attraction",
				"Public Square", "Amusement", "Landmark", "Statue & Fountain", "Public Places & Attractions", "Museum",
				"Park", "Lake", "Mountain", "National Park", "Attractions/Things to Do", "Tourist Information",
				"Tour Guide", "Water Park", "Outdoor Recreation", "Theatre", "Tours & Sightseeing", "Auditorium",
				"Zoo & Aquarium", "State Park", "History Museum", "Theme Park", "Catholic Church", "Bridge", "Island",
				"Cabin", "Church", "Cruise", "Circus", "Art Gallery", "Wildlife Sanctuary", "Cruise Excursions",
				"Buddhist Temple", "River", "Environmental Conservation", "Modern Art Museum", "Public Places" };

		for(int i = 0; i < categories.length; i++){
			String cat = categories[i];
			System.out.println("<p><input type='checkbox' value='" + cat.replaceAll(" ", "_") + "' name='category'/>"+cat+"</p>");
		}
	}
}
