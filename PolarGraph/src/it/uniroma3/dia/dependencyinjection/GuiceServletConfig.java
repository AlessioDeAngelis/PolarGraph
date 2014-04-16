package it.uniroma3.dia.dependencyinjection;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

public class GuiceServletConfig extends GuiceServletContextListener {

	  @Override
	  protected Injector getInjector() {
	    return Guice.createInjector(new ServletModule());
	  }
	}
