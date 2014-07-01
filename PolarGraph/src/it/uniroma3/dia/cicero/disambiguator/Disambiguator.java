package it.uniroma3.dia.cicero.disambiguator;

import com.google.inject.Inject;

import it.uniroma3.dia.cicero.controller.PropertiesManager;
import it.uniroma3.dia.cicero.graph.model.PolarPlace;
import it.uniroma3.dia.cicero.rest.RestManager;

public interface Disambiguator {

	public PolarPlace disambiguatePlace(PolarPlace polarPlace);

}
