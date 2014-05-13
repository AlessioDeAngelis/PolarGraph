package it.uniroma3.dia.polar.servlet.actions;

import it.uniroma3.dia.dependencyinjection.RecommenderFactory;
import it.uniroma3.dia.polar.controller.PolarFacade;
import it.uniroma3.dia.polar.graph.model.RecommendedObject;
import it.uniroma3.dia.polar.persistance.CypherRepository;
import it.uniroma3.dia.polar.recommender.Recommender;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Injector;

public class RecommendAction extends Action {

	@Override
	public String executeAction(HttpServletRequest request) throws ServletException {
		String rankerName = request.getParameter("rankerType");// from
																// chooseRecommender.jsp
																// form
		List<String> categories = (ArrayList<String>) request.getSession().getAttribute("choosenCategories");
		// Injector injector = Guice.createInjector(new
		// PolarServletModule(request.getServletContext(),RankerType.fromString(rankerName)));

		// Guice.createInjector(Modules.override(new
		// PolarServletModule(request.getServletContext(),RankerType.fromString(rankerName))).with(new
		// PolarServletModule(request.getServletContext(),RankerType.fromString(rankerName))));

		String userId = (String) request.getSession().getAttribute("fb_user_id");
		Injector injector = (Injector) request.getSession().getAttribute("injector");

		PolarFacade polarFacade = injector.getInstance(PolarFacade.class);
		RecommenderFactory recommenderFactory = injector.getInstance(RecommenderFactory.class);
		CypherRepository repository = injector.getInstance(CypherRepository.class);
		Recommender recommender = recommenderFactory.createRecommender(rankerName, injector);
		if (categories != null) {
			recommender.setCategories(categories);
		}
		polarFacade.setRecommender(recommender);
		List<RecommendedObject> objs = polarFacade.recommendPlace(userId);
		request.getSession().setAttribute("recommendedObjects", objs);
		request.getSession().setAttribute("injector", injector);

		return "recommended_objects_returned";
	}

}
