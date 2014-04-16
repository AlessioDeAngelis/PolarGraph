package it.uniroma3.dia.polar.servlet.actions;

import it.uniroma3.dia.dependencyinjection.PolarModule;
import it.uniroma3.dia.polar.controller.PolarFacade;
import it.uniroma3.dia.polar.graph.model.RecommendedObject;
import it.uniroma3.dia.polar.persistance.CypherRepository;

import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class RecommendAction extends Action{

	@Override
	public String executeAction(HttpServletRequest request)
			throws ServletException {
		Injector injector = (Injector)request.getSession().getAttribute("injector");
		String userId = (String)request.getSession().getAttribute("fb_user_id");

		PolarFacade polarController = injector.getInstance(PolarFacade.class);
		List<RecommendedObject> objs = polarController.recommendPlace(userId);
		request.getSession().setAttribute("recommendedObjects", objs);
		
	
		
		return "recommended_objects_returned";
	}

}
