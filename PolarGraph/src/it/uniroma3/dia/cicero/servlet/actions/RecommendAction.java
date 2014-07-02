package it.uniroma3.dia.cicero.servlet.actions;

import it.uniroma3.dia.cicero.controller.CiceroFacade;
import it.uniroma3.dia.cicero.dependencyinjection.RecommenderFactory;
import it.uniroma3.dia.cicero.graph.model.Category;
import it.uniroma3.dia.cicero.graph.model.RecommendedObject;
import it.uniroma3.dia.cicero.persistance.CypherRepository;
import it.uniroma3.dia.cicero.recommender.Recommender;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Injector;

public class RecommendAction extends Action {

	@Override
	public String executeAction(HttpServletRequest request) throws ServletException {

		// from chooseRecommender.jsp form
		String[] rankerSocialNames = request.getParameterValues("rankerSocialType");
		String[] rankerDbpediaNames = request.getParameterValues("rankerDbpediaType");
		String[] rankerEuropeanaNames = request.getParameterValues("rankerEuropeanaType");
		request.getSession().setAttribute("recommenderNumber", request.getParameter("recommenderNumber"));
		//TODO: remove this initilization of categories since now the sistem calculates the categories for the user
		List<String> categories = (ArrayList<String>) request.getSession().getAttribute("choosenCategories");
		
		
		// Injector injector = Guice.createInjector(new
		// PolarServletModule(request.getServletContext(),RankerType.fromString(rankerName)));

		// Guice.createInjector(Modules.override(new
		// PolarServletModule(request.getServletContext(),RankerType.fromString(rankerName))).with(new
		// PolarServletModule(request.getServletContext(),RankerType.fromString(rankerName))));

		String userId = (String) request.getSession().getAttribute("fb_user_id");
		Injector injector = (Injector) request.getSession().getAttribute("injector");

		CiceroFacade ciceroFacade = injector.getInstance(CiceroFacade.class);
		RecommenderFactory recommenderFactory = injector.getInstance(RecommenderFactory.class);
		
		/* let the system find the user favourite categories*/
		List<Category> userFavouriteCategories = ciceroFacade.calculateUserFavouriteCategories(userId);
		categories = new ArrayList<String>();
		for(Category category : userFavouriteCategories){
			categories.add(category.getName());
		}

		// firstly add the social recommender. At least one recommender must be
		// present in the list
		if (rankerSocialNames == null || rankerSocialNames.length == 0) {
			Recommender recommender = recommenderFactory.createRecommender("naive", injector);
			ciceroFacade.addRecommenderToTheRecommenderChainManager(recommender);
		}
		for (int i = 0; i < rankerSocialNames.length; i++) {
			String socialRecommenderName = rankerSocialNames[i];
			Recommender socialRecommender = recommenderFactory.createRecommender(socialRecommenderName, injector);
			if (categories != null) {
				socialRecommender.setCategories(categories);
			}
			ciceroFacade.addRecommenderToTheRecommenderChainManager(socialRecommender);
		}

		// secondly a dbpedia recommender. It is not required the presence of a
		// dbpedia recommender
		if (rankerDbpediaNames != null) {
			for (int i = 0; i < rankerDbpediaNames.length; i++) {
				String dbpediaRecommenderName = rankerDbpediaNames[i];
				Recommender dbpediaRecommender = recommenderFactory.createRecommender(dbpediaRecommenderName, injector);
				ciceroFacade.addRecommenderToTheRecommenderChainManager(dbpediaRecommender);
			}
		}

		// at the end an europeana semantic recommender. It is not required the
		// presence of an europeana recommender but it is strongly recommended
		if (rankerEuropeanaNames != null) {
			for (int i = 0; i < rankerEuropeanaNames.length; i++) {
				String europeanaRecommenderName = rankerEuropeanaNames[i];
				Recommender europeanaRecommender = recommenderFactory.createRecommender(europeanaRecommenderName,
						injector);
				ciceroFacade.addRecommenderToTheRecommenderChainManager(europeanaRecommender);
			}
		}
		List<RecommendedObject> objs = ciceroFacade.recommendPlace(userId);
		request.getSession().setAttribute("recommendedObjects", objs);

		return "recommended_objects_returned";
	}
}
