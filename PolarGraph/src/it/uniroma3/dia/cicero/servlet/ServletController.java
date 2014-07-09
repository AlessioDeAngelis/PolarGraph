package it.uniroma3.dia.cicero.servlet;

import it.uniroma3.dia.cicero.controller.CiceroFacade;
import it.uniroma3.dia.cicero.servlet.actions.Action;
import it.uniroma3.dia.cicero.servlet.actions.ChooseSocialCategoriesAction;
import it.uniroma3.dia.cicero.servlet.actions.ClearGraphDatabaseAction;
import it.uniroma3.dia.cicero.servlet.actions.LoginAction;
import it.uniroma3.dia.cicero.servlet.actions.RecommendAction;
import it.uniroma3.dia.cicero.servlet.actions.StoreFacebookFriendDataAction;
import it.uniroma3.dia.cicero.servlet.actions.StoreFacebookUserDataAction;
import it.uniroma3.dia.cicero.servlet.actions.StoreRecommenderRatingAction;
import it.uniroma3.dia.cicero.servlet.actions.WelcomeAction;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * Servlet implementation class HelloServlet
 */
@Singleton
@WebServlet("*.do")
public class ServletController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Map<String, Class> command2actionClass;
	private Map<String, String> result2page;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ServletController() {
		super();
	}

	public void init() {
		Class clazz = Action.class;
		this.command2actionClass = new HashMap<String, Class>();
		this.command2actionClass.put("/fblogin.do", LoginAction.class);
		this.command2actionClass.put("/welcome.do", WelcomeAction.class);
		this.command2actionClass.put("/recommend.do", RecommendAction.class);
		this.command2actionClass.put("/chooseCategories.do",
				ChooseSocialCategoriesAction.class);
		this.command2actionClass.put("/storeFacebookUserData.do",
				StoreFacebookUserDataAction.class);
		this.command2actionClass.put("/storeFacebookFriendData.do",
				StoreFacebookFriendDataAction.class);
		this.command2actionClass.put("/clearGraphDatabase.do",
				ClearGraphDatabaseAction.class);
		this.command2actionClass.put("/storeRecommenderRating.do",
				StoreRecommenderRatingAction.class);
		this.result2page = new HashMap<String, String>();
		this.result2page.put("autenticazioneriuscita", "/main.jsp");
		// this.result2page.put("categories_choosen", "/chooseRecommender.jsp");
		this.result2page
				.put("recommended_objects_returned", "/recommended.jsp");
		this.result2page.put("facebook_login_ok", "/userLogged.jsp");
		this.result2page.put("chooseSocialCategories",
				"/chooseSocialCategories.jsp");
		this.result2page.put("userDataStored", "/userDataStored.jsp");
		this.result2page.put("friendsDataStored", "/friendsDataStored.jsp");
		this.result2page.put("graphDatabaseCleared",
				"/graphDatabaseCleared.jsp");
		this.result2page.put("rating_stored", "/ratingStored.jsp");
		this.result2page.put("noLog", "/index.jsp");

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String nextPage = "";
		try {
			String servletPath = request.getServletPath();
			Class clazz = this.command2actionClass.get(servletPath);
//			Injector injector = (Injector) request.getSession().getAttribute(
//					"injector");
//			if (injector == null) {
//				nextPage = "/index.jsp";
//			} else {
//				CiceroFacade ciceroFacade = injector
//						.getInstance(CiceroFacade.class);
//				if (ciceroFacade == null) {
//					nextPage = "/index.jsp";
//				} else	
			if (clazz == null) {
					nextPage = "/classNotFound.jsp";
				} else {
					Action action = null;
					String actionName = clazz.getName();

					action = (Action) Class.forName(actionName).newInstance();
					String actionResult = action.executeAction(request);
					nextPage = this.result2page.get(actionResult);
				}
//			}
		} catch (InstantiationException e) {
			nextPage = "/error.jsp";
		} catch (IllegalAccessException e) {
			nextPage = "/error.jsp";
		} catch (ClassNotFoundException e) {
			nextPage = "/classNotFound.jsp";
		}
		ServletContext sc = this.getServletContext();
		RequestDispatcher rd = sc.getRequestDispatcher(nextPage);
		rd.forward(request, response);
	}

}
