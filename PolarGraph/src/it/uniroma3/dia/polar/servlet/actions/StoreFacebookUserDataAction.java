package it.uniroma3.dia.polar.servlet.actions;

import it.uniroma3.dia.polar.controller.PolarFacade;
import it.uniroma3.dia.polar.persistance.FacebookRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Injector;
import com.restfb.types.User;

public class StoreFacebookUserDataAction extends Action {

	@Override
	public String executeAction(HttpServletRequest request) throws ServletException {
		Injector injector = (Injector) request.getSession().getAttribute("injector");
		User user = (User)request.getSession().getAttribute("facebookUser");
		String fbUserId = user.getId();
		PolarFacade polarController = injector.getInstance(PolarFacade.class);
		
		//store my info
//		polarController.readUserFromFacebookAndStore(fbUserId);
		polarController.readVisitedPlacesFromFacebookAndStore(fbUserId);
//		polarController.readFriendsFromFacebookAndStore(fbUserId);
		return "userDataStored";
	}

}
