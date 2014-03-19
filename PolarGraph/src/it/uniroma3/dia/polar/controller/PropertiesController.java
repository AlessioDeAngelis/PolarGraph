package it.uniroma3.dia.polar.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.google.inject.Inject;

public class PropertiesController {
	private Properties properties;

	@Inject
	public PropertiesController() {
		loadProperties();
	}

	private void loadProperties() {
		this.properties = new Properties();
		try {
			FileInputStream fis = new FileInputStream("data/polar_graph.properties");
			properties.load(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Properties getProperties() {
		return this.properties;
	}
}
