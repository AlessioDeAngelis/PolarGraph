package it.uniroma3.dia.polar.disambiguator;

import com.google.inject.Inject;

import it.uniroma3.dia.polar.controller.PropertiesManager;
import it.uniroma3.dia.polar.graph.model.PolarPlace;
import it.uniroma3.dia.polar.rest.RestManager;

public interface Disambiguator {

	public PolarPlace disambiguatePlace(PolarPlace polarPlace);

}
