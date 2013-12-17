package it.uniroma3.dia.polar.graph.model;

import java.util.ArrayList;
import java.util.List;

public class Person {
	private String name, surname, id;
	private List<Person> friends;
	private List<PolarPlace> visitedPlaces;
	public Person() {
		this.id = "";
		this.name = "";
		this.surname = "";
		this.setFriends(new ArrayList<Person>());
		this.visitedPlaces = new ArrayList<PolarPlace>();
	}

	public Person(String id, String name, String surname) {
		super();
		this.name = name;
		this.surname = surname;
		this.id = id;
		this.friends = new ArrayList<Person>();
		this.visitedPlaces = new ArrayList<PolarPlace>();
	}	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Person> getFriends() {
		return friends;
	}
	
	public void setFriends(List<Person> friends) {
		this.friends = friends;
	}
	
	public void addFriend(Person person){
		this.friends.add(person);
	}

	public List<PolarPlace> getVisitedPlaces() {
		return visitedPlaces;
	}

	public void setVisitedPlaces(List<PolarPlace> visitedPlaces) {
		this.visitedPlaces = visitedPlaces;
	}
	
	public void addVisitedPlace(PolarPlace place){
		this.visitedPlaces.add(place);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((surname == null) ? 0 : surname.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person other = (Person) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (surname == null) {
			if (other.surname != null)
				return false;
		} else if (!surname.equals(other.surname))
			return false;
		return true;
	}
}
