package it.uniroma3.dia.cicero.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import com.google.inject.Inject;

public class PropertiesManager {
	private Properties properties;

	@Inject
	public PropertiesManager() {

	}

	public Properties getProperties(String path) {
		if (this.properties == null) {
			try {
				this.properties = new Properties();
				FileInputStream fis = new FileInputStream(path);
				properties.load(fis);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this.properties;
	}
}