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

import org.mortbay.jetty.Server;

import com.flaptor.util.remote.WebServer;

/**
 * Embedded HTTPServer that uses a webapp structured directory   
 * 
 * @author Martin Massera
 */
public class EmbeddedWebAppHTTPServer extends WebServer{
	/**
     * Constructor, creates and starts the server.
	 * @param port the port where the server will listen
	 * @param webappPath the path where the webapp is contained
	 * @param contextPath the context where the webapp will be placed (must start with "/")
	 *    for instance http://server:port/context/test.jsp
	 */
	public EmbeddedWebAppHTTPServer(int port, String webappPath, String contextPath) {
		super(port);
		addWebAppHandler(contextPath, webappPath);
        start();
	}
	
    public static void main(String[] args) {
        int port=0;
        String webappPath=null;
        String context=null;
        try {
            port= Integer.parseInt(args[0]);
            webappPath= args[1];
            context= args[2];
        } catch (Exception e) {
            usageAndExit();
        }
        new EmbeddedWebAppHTTPServer(port, webappPath, context);
    }

    private static void usageAndExit() {
        System.err.println("Usage: <port> <webappPath> <contextPath> ");
        System.err.println("port: the port where the server will listen");
        System.err.println("webappPath: the path where the webapp is contained");
        System.err.println("contextPath: the context where the webapp will be installed (must start with \"/\"");
        System.err.println("for instance http://server:port/context/lalala.jsp");
        System.exit(-1);
    }

}
