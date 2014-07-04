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

public class StoreRecommenderRatingAction extends Action {

	@Override
	public String executeAction(HttpServletRequest request) throws ServletException {
		//parameters from recommenderMain.jsp and recommender.jsp
		String rating = (String) request.getParameter("recommender_rating");		
		String fbUserId = (String) request.getSession().getAttribute("fb_user_id");
		String recommenderId = (String) request.getSession().getAttribute("recommenderNumber");
		String novelty = (String) request.getParameter("novelty");
		String serendipity = (String) request.getParameter("serendipity");
		String diversity = (String) request.getParameter("diversity");
		Injector injector = (Injector) request.getSession().getAttribute("injector");
		
		CiceroFacade ciceroFacade = injector.getInstance(CiceroFacade.class);
		ciceroFacade.storeUserRatingForRecommendation(fbUserId, Integer.parseInt(recommenderId), Integer.parseInt(rating),novelty,serendipity,diversity);
	
		return "rating_stored";
	}
}
