package it.uniroma3.dia.polar.graph.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PolarPlace {
	/**
	 * The name of the place
	 * */
	private String name;
	/**
	 * the facebook id of the place
	 * */
	private String id;

	private Location location;

	private List<Category> categories;
	/**
	 * The likes count given by all the community
	 * */
	private long likesCount;
	/**
	 * The id of the person that like the place
	 * The direction is "likedBy" and not "likes" because of the way how we retrieve data from facebook
	 * */
	private List<String> likedBy;

	public PolarPlace() {
		super();
		this.name = "";
		this.id = "";
		this.likesCount = 0;
		this.location = new Location();
		this.categories = new ArrayList<Category>();
		this.likedBy = new ArrayList<String>();
	}

	public PolarPlace(String name, String id, Location location, List<Category> categories,long likesCount, List<String> likedBy) {
		super();
		this.name = name;
		this.id = id;
		this.location = location;
		this.categories = categories;
		this.likesCount = 0;
		this.likedBy = likedBy;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void addCategory(Category category) {
		this.categories.add(category);
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public long getLikesCount() {
		return likesCount;
	}

	public void setLikesCount(long likesCount) {
		this.likesCount = likesCount;
	}

	public List<String> getLikedBy() {
		return likedBy;
	}

	public void setLikedBy(List<String> likedBy) {
		this.likedBy = likedBy;
	}
	
	public void addLikedBy(String personThatLikesIt){
		this.likedBy.add(personThatLikesIt);
	}

	@Override
	public String toString() {
		return "PolarPlace [name=" + name + ", id=" + id + ", location=" + location + ", categories=" + categories
				+ ", likesCount=" + likesCount + ", likedBy=" + likedBy + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((categories == null) ? 0 : categories.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((likedBy == null) ? 0 : likedBy.hashCode());
		result = prime * result + (int) (likesCount ^ (likesCount >>> 32));
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		PolarPlace other = (PolarPlace) obj;
		if (categories == null) {
			if (other.categories != null)
				return false;
		} else if (!categories.equals(other.categories))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (likedBy == null) {
			if (other.likedBy != null)
				return false;
		} else if (!likedBy.equals(other.likedBy))
			return false;
		if (likesCount != other.likesCount)
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	

}
