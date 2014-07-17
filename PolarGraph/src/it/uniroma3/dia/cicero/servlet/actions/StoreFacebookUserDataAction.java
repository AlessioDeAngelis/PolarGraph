package it.uniroma3.dia.cicero.servlet.actions;

import it.uniroma3.dia.cicero.controller.CiceroFacade;
import it.uniroma3.dia.cicero.persistance.FacebookRepository;

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
		CiceroFacade ciceroFacade = injector.getInstance(CiceroFacade.class);
		
		//store the user in the evaluation relational db
		ciceroFacade.storeUserDataInEvaluationRepository(user);
		//store my info on neo4j
		try{
		ciceroFacade.readUserFromFacebookAndStore(fbUserId);
		ciceroFacade.readVisitedPlacesFromFacebookAndStore(fbUserId);
		ciceroFacade.readFriendsFromFacebookAndStore(fbUserId);
		}catch(Exception e){
			//lock exception
		}
		return "userDataStored";
	}

}
