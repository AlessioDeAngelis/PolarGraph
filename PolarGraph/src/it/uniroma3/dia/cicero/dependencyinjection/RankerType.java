package it.uniroma3.dia.cicero.dependencyinjection;

public enum RankerType {
	NAIVE, SEMANTICBASE;
	
	public static RankerType fromString(String s){
		RankerType rankerEnum = NAIVE;
		if(s.equals("semanticbase")){
			rankerEnum = SEMANTICBASE;
		}
		return rankerEnum;
	}
}
