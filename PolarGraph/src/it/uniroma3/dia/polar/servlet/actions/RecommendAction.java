package it.uniroma3.dia.polar.servlet.actions;

import it.uniroma3.dia.dependencyinjection.PolarModule;
import it.uniroma3.dia.dependencyinjection.PolarServletModule;
import it.uniroma3.dia.dependencyinjection.RankerType;
import it.uniroma3.dia.dependencyinjection.TemporaryRankerModule;
import it.uniroma3.dia.polar.controller.PolarFacade;
import it.uniroma3.dia.polar.graph.model.RecommendedObject;
import it.uniroma3.dia.polar.persistance.CypherRepository;

import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

public class RecommendAction extends Action{

	@Override
	public String executeAction(HttpServletRequest request)
			throws ServletException {
		String rankerName = request.getParameter("rankerType");//from homepage.jsp form
//		Injector injector = Guice.createInjector(new PolarServletModule(request.getServletContext(),RankerType.fromString(rankerName)));
		
//		Guice.createInjector(Modules.override(new PolarServletModule(request.getServletContext(),RankerType.fromString(rankerName))).with(new PolarServletModule(request.getServletContext(),RankerType.fromString(rankerName))));	

		
		String userId = (String)request.getSession().getAttribute("fb_user_id");
		Injector injector = (Injector)request.getSession().getAttribute("injector");
//		injector.createChildInjector(new TemporaryRankerModule(request.getServletContext(),RankerType.fromString(rankerName)));
		PolarFacade polarController = injector.getInstance(PolarFacade.class);
		List<RecommendedObject> objs = polarController.recommendPlace(userId);
		request.getSession().setAttribute("recommendedObjects", objs);
		request.getSession().setAttribute("injector", injector);
		
		return "recommended_objects_returned";
	}

}
