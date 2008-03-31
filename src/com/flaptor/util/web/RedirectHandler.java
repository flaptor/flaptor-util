package com.flaptor.util.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.handler.AbstractHandler;

/**
 * A handler for redirects. This handler is added to the web server to redirect one path to another.
 */
public class RedirectHandler extends AbstractHandler {

	private String from, to; 
	
	/**
	 * Constructor
	 * @param from the path to redirect from.
	 * @param to the path to redirect to.
	 */
	public RedirectHandler(String from, String to) {
		this.from = from;
		this.to = to; 
	}

	@Override
	/**
	 * Handle the request.
	 */
	public void handle(String arg0, HttpServletRequest arg1,
			HttpServletResponse arg2, int arg3) throws IOException,
			ServletException {
		if (arg1.getRequestURI().matches(from)) {
			arg2.sendRedirect(to);
		}
	}
}
