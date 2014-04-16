package it.uniroma3.dia.polar.servlet;

import it.uniroma3.dia.polar.servlet.actions.Action;
import it.uniroma3.dia.polar.servlet.actions.LoginAction;
import it.uniroma3.dia.polar.servlet.actions.WelcomeAction;

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
		this.command2actionClass.put("/login.do", LoginAction.class);
		this.command2actionClass.put("/welcome.do", WelcomeAction.class);

		this.result2page = new HashMap<String, String>();
		this.result2page.put("autenticazioneriuscita", "/main.jsp");
		this.result2page.put("homepage", "/homepage.jsp");
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
			Class clazz = this.command2actionClass
					.get(servletPath);

			if (clazz == null) {
				nextPage = "/classNotFound.jsp";
			} else {
				Action action = null;
				String actionName = clazz.getName();

				action = (Action) Class.forName(actionName).newInstance();
				String actionResult = action.executeAction(request);
				nextPage = this.result2page.get(actionResult);
			}
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