package com.flaptor.util;

import javax.servlet.http.HttpServletRequest;


/**
 * Utility methods for webapps
 */ 
public class WebAppUtil {
	
	/**
	 * get a parameter value from the request, and if not there from the session, and if not apply default value.
	 * Next store it in the session
	 * 
	 * @return the value
	 */
	public static String getParameterSessionValue(HttpServletRequest request, String name, String defaultValue){
		String value = request.getParameter(name);
		if (value == null) {
			value = (String)request.getSession().getAttribute(name);
			if (value == null) value = defaultValue;
		}
		request.getSession().setAttribute(name, value);
		return value;
	}

	/**
	 * get parameter values (as String[])from the request, and if not there from the session, and if not apply default values.
	 * Next store them in the session
	 * 
	 * @return the values
	 */
	public static String[] getParameterSessionValues(HttpServletRequest request, String name, String[] defaultValues){
		String[] values = request.getParameterValues(name);
		if (values == null) {
			values = (String[])request.getSession().getAttribute(name);
			if (values == null) values = defaultValues;
		}
		request.getSession().setAttribute(name, values);
		return values;
	}

}
