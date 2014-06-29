package it.uniroma3.dia.cicero.servlet.actions;

import java.util.ArrayList;
import java.util.List;

import it.uniroma3.dia.cicero.controller.CiceroFacade;
import it.uniroma3.dia.cicero.dependencyinjection.RecommenderFactory;
import it.uniroma3.dia.cicero.graph.model.RecommendedObject;
import it.uniroma3.dia.cicero.recommender.Recommender;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Injector;

public class ChooseSocialCategoriesAction extends Action {

	@Override
	public String executeAction(HttpServletRequest request) throws ServletException {

		String[] categories = request.getParameterValues("category");
		List<String> categoriesList = new ArrayList<String>();
		if (categories != null && categories.length > 0) {
			for (int i = 0; i < categories.length; i++) {
				categoriesList.add(categories[i].replace("_", " "));
			}
		}

		request.getSession().setAttribute("choosenCategories", categoriesList);

		return "categories_choosen";
	}

}
