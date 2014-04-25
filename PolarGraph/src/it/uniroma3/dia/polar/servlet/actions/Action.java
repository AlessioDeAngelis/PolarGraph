package it.uniroma3.dia.polar.servlet.actions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public abstract class Action {
	/**
	 * @return the result of the action (for example "OK" or "error")
	 * */
 public abstract String executeAction(HttpServletRequest request) throws ServletException;
}
