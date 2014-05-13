package it.uniroma3.dia.polar.graph.model;

import java.io.Serializable;

public class RecommendedObject implements Serializable{

	private static final long serialVersionUID = 1659709820237051388L;
	private String id;
	private String name;
	private String uri;
	private double score;
	private String mediaUrl;
	/**
	 * The concept that originated this Recommended Object. For example: you are
	 * recommended Geirangerfjord since you like Geiranger
	 * */
	private String why;
	private String creator;
	private String provider;
	private String source;

	public RecommendedObject() {
		super();
		this.id = "";
		this.name = "";
		this.uri = "";
		this.score = 0d;
		this.mediaUrl = "";
		this.why = "";
		this.creator = "";
		this.provider = "";
		this.source = "";
	}

	public RecommendedObject(String id, String name, String uri) {
		super();
		this.id = id;
		this.name = name;
		this.uri = uri;
		this.score = 0d;
		this.mediaUrl = "";
		this.why = "";
		this.creator = "";
		this.provider = "";
		this.source = "";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public String getMediaUrl() {
		return mediaUrl;
	}

	public void setMediaUrl(String mediaUrl) {
		this.mediaUrl = mediaUrl;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((mediaUrl == null) ? 0 : mediaUrl.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		long temp;
		temp = Double.doubleToLongBits(score);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	public String getWhy() {
		return why;
	}

	public void setWhy(String why) {
		this.why = why;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecommendedObject other = (RecommendedObject) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (mediaUrl == null) {
			if (other.mediaUrl != null)
				return false;
		} else if (!mediaUrl.equals(other.mediaUrl))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Double.doubleToLongBits(score) != Double.doubleToLongBits(other.score))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RecommendedObject [id=" + id + ", name=" + name + ", uri=" + uri + ", score=" + score + ", mediaUrl="
				+ mediaUrl + "]";
	}

}
