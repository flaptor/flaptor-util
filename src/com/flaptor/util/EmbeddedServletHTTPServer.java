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

import javax.servlet.http.HttpServlet;

import org.mortbay.jetty.Server;

import com.flaptor.util.remote.WebServer;

/**
 * Embedded HTTPServer that uses a webapp structured directory
 * @author Martin Massera
 */
public class EmbeddedServletHTTPServer extends WebServer {

	/**
     * Constructor, creates and starts the server (no need to call start)
     * 
	 * @param port the port where the server will listen
	 * @param webappPath the path where the webapp is contained
	 * @param contextPath the context where the webapp will be placed (must start with "/")
	 *    for instance http://server:port/context/test.jsp
	 */
	public EmbeddedServletHTTPServer(int port, HttpServlet servlet, String contextPath) {
		super(port);
		addServletHandler(contextPath, servlet);
		start();
	}
	
    public static void main(String[] args) {
        int port=0;
        String servletClass=null;
        String context=null;
        try {
            port= Integer.parseInt(args[0]);
            servletClass= args[1];
            context= args[2];
        } catch (Exception e) {
            usageAndExit();
        }
        new EmbeddedServletHTTPServer(port, (HttpServlet)ClassUtil.instance(servletClass), context); 
    }

    private static void usageAndExit() {
        System.err.println("Usage: <port> <ServletClass> <contextPath> ");
        System.err.println("port: the port where the server will listen");
        System.err.println("ServletClass: the classname of the servlet");
        System.err.println("contextPath: the context where the webapp will be installed (must start with \"/\"");
        System.err.println("for instance http://server:port/context/lalala");
        System.exit(-1);
    }

}
