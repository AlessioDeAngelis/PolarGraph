package it.uniroma3.dia.polar.servlet.actions;

import it.uniroma3.dia.dependencyinjection.PolarServletModule;
import it.uniroma3.dia.polar.controller.PolarFacade;
import it.uniroma3.dia.polar.controller.PropertiesManager;
import it.uniroma3.dia.polar.persistance.CypherRepository;

import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class WelcomeAction extends Action {

	@Override
	public String executeAction(HttpServletRequest request)
			throws ServletException {
		Properties props;
		Injector injector = Guice.createInjector(new PolarServletModule(request.getServletContext()));
		PropertiesManager propertiesController = injector.getInstance(PropertiesManager.class);
		props = propertiesController.getProperties(request.getServletContext().getRealPath("/")+"data/polar_graph.properties");
		
		String fbUserId = props.getProperty("fb_user_id");
		
		PolarFacade polarFacade = injector.getInstance(PolarFacade.class);
		CypherRepository repository = injector.getInstance(CypherRepository.class);
		repository.setDbPath(request.getServletContext().getRealPath("/")+repository.getDbPath());
		request.getSession().setAttribute("facade", polarFacade);	
		request.getSession().setAttribute("fb_user_id", fbUserId );	
		request.getSession().setAttribute("injector", injector);

		return "homepage";
	}
	
	

}
