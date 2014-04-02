package it.uniroma3.it.dia.polar.persistance;

import static org.junit.Assert.fail;
import it.uniroma3.dia.polar.graph.model.Person;
import it.uniroma3.dia.polar.persistance.CypherRepository;

import org.junit.Before;
import org.junit.Test;

public class CypherRepositoryTest {

	private final static String dbPath = "data/db/polar.graph";

	public static void main(String[] args) {
		testCountFriends();
	}
	
	public static void testCountFriends(){
		CypherRepository repository = new CypherRepository(dbPath);
		repository.startDB();
		repository.findPlacesVisitedByTheUserAndCountFriends("1366205360");
		repository.stopDB();
	}
	
	public static void generalTest(){
		CypherRepository repository = new CypherRepository(dbPath);
		repository.startDB();
		for (int i = 0; i < 10; i++) {
			Person person = new Person();
			person.setId("" + i);
			person.setName("Alessio " + i);
			person.setSurname("De Angelis");
			repository.insertPersonNode(person);
//			repository.mergeRelationship(person);
//			repository.createUniqueRelationship(person);
//			repository.insertPersonNodeAndFind(person);
		}
		System.out.println("end test");
//		repository.findPersonByIdCypher(0);
		repository.stopDB();
	}

}
