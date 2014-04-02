package it.uniroma3.it.dia.polar.persistance;

import it.uniroma3.dia.polar.graph.model.Person;
import it.uniroma3.dia.polar.persistance.CypherRepository;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class CypherRepositoryJunitTest {

	private CypherRepository repository;
	private final static String dbPath = "data/db/test.graph";

	@Before
	public void initialize() {
		this.repository = new CypherRepository(dbPath);
	}

	@Test
	public void insertPersonAndFriendsTest() {
		long start = System.currentTimeMillis();
		this.repository.startDB();
		Person alessio = new Person();
		alessio.setId("31189");
		alessio.setName("Alessio");
		alessio.setSurname("De Angelis");
		List<Person> friends = new ArrayList<Person>();
		
		for (int i = 0; i < 200; i++) {
			Person friend = new Person();
			friend.setId(""+i);
			friend.setName("Friend #"+i);
			friend.setSurname("Awesome");
			friends.add(friend);
		}
		alessio.setFriends(friends);
		this.repository.insertPersonAndFriends(alessio);
		this.repository.stopDB();
		long end = System.currentTimeMillis();
		System.out.println("TIME ELAPSED " + (end-start) + " msec");
	}

}

