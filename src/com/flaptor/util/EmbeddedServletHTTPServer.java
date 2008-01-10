package com.flaptor.util;

import javax.servlet.http.HttpServlet;

import org.mortbay.jetty.Server;

import com.flaptor.util.remote.WebServer;

/**
 * Embedded HTTPServer that uses a webapp structured directory   
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
