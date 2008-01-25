/*
Copyright 2008 Flaptor (flaptor.com) 

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

    http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License.
*/

package com.flaptor.util;

import javax.servlet.http.HttpServletRequest;


/**
 * Utility methods for webapps
 * 
 * @author Martin Massera
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
