package com.flaptor.util;

import org.mortbay.jetty.Server;

import com.flaptor.util.remote.WebServer;

/**
 * Embedded HTTPServer that uses a webapp structured directory   
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
