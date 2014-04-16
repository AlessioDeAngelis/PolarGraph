package it.uniroma3.dia.polar.servlet.actions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public class LoginAction extends Action{

	@Override
	public String executeAction(HttpServletRequest request)
			throws ServletException {
		return "OK";
	}

}
