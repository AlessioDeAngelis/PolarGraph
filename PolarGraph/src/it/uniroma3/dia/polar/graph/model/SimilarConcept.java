package it.uniroma3.dia.polar.graph.model;


public class SimilarConcept {
	private String cm;
	private String cx;
	private double similarity;

	public SimilarConcept(String cm, String cx, double similarity) {
		super();
		this.cm = cm;
		this.cx = cx;
		this.similarity = similarity;
	}

	public String getCm() {
		return cm;
	}

	public void setCm(String cm) {
		this.cm = cm;
	}

	public String getCx() {
		return cx;
	}

	public void setCx(String cx) {
		this.cx = cx;
	}

	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cm == null) ? 0 : cm.hashCode());
		result = prime * result + ((cx == null) ? 0 : cx.hashCode());
		long temp;
		temp = Double.doubleToLongBits(similarity);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		SimilarConcept other = (SimilarConcept) obj;
		if (cm == null) {
			if (other.cm != null)
				return false;
		} else if (!cm.equals(other.cm))
			return false;
		if (cx == null) {
			if (other.cx != null)
				return false;
		} else if (!cx.equals(other.cx))
			return false;
		if (Double.doubleToLongBits(similarity) != Double.doubleToLongBits(other.similarity))
			return false;
		return true;
	}

}
