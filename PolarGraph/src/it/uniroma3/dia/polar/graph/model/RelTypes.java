package it.uniroma3.dia.polar.graph.model;

import org.neo4j.graphdb.RelationshipType;

public enum RelTypes implements RelationshipType{
			KNOWS,
			REVIEWS,
			POI_DESCRIPTION,
			HAS_CONTEXT
}
