package it.uniroma3.dia.cicero.graph.model;


public class RankedPlace extends PolarPlace {
	private double score;

	public RankedPlace() {
		super();
		score = 0;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
	
}
