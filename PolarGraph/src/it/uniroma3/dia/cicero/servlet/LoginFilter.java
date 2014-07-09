package it.uniroma3.dia.cicero.servlet;

import it.uniroma3.dia.cicero.controller.CiceroFacade;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.inject.Injector;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter("*.do")
public class LoginFilter implements Filter {
	private ServletContext application;

	/**
	 * Default constructor.
	 */
	public LoginFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
//		// TODO Auto-generated method stub
//		// place your code here
//		HttpServletRequest req = (HttpServletRequest) request;
//		HttpSession session = req.getSession();
//		RequestDispatcher rd = this.application
//				.getRequestDispatcher("/index.jsp");
//
//		Injector injector = (Injector) session.getAttribute("injector");
//		String accessToken = (String) session.getAttribute("accessToken");
//
//		if (injector == null || !((String)request.getAttribute("logged")).equals("true")) {
//			rd.forward(request, response);
//		} else {
//			CiceroFacade ciceroFacade = injector
//					.getInstance(CiceroFacade.class);
//			System.out.println(ciceroFacade == null);
//			if (ciceroFacade == null) {
//				request.setAttribute("logged", "true");
//				rd.forward(request, response);
//			} // pass the request along the filter chain
//			else {
				chain.doFilter(request, response);
//			}
//		}

	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
		this.application = fConfig.getServletContext();
	}

}
