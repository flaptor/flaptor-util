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

package com.flaptor.util.remote;


import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.HandlerWrapper;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.xml.XmlConfiguration;

import com.flaptor.util.FileUtil;
import com.flaptor.util.Execute;

/**
 * A WebServer to manage a single jetty AbstractHandler.
 * 
 * @author Martin Massera
 */
public class WebServer extends AServer {

	private static Logger logger = Logger.getLogger(Execute.whoAmI());

	private final Server webserver;
	private final Map<String, AbstractHandler> handlers = new HashMap<String, AbstractHandler>(); 

	/**
	 * Runs the server from the command line.
	 * 
	 * @param args
	 *            First: Handler class. Must have a default constructor.
     *            Second: port
	 */
	public static void main(final String[] args) {
		if (args.length != 2) {
			System.err.println("usage: WebServer [handler class] [port]");
			System.exit(-1);
		}

        Execute.configureLog4j();

		Object handler = null;
		try {
			handler = Class.forName(args[0]).newInstance();
		} catch (Exception e) {
			System.err.println(e);
			System.exit(-1);
		}
		WebServer server = new WebServer(Integer.parseInt(args[1]));
		server.addHandler("/", (AbstractHandler)handler);
		server.start();
	}

	/**
	 * Constructor. Will try to load configuration from jetty.xml in the classpath
	 * 
	 * @param p the port on which to listen
	 */
	public WebServer(final int p) {
	    this(p, "jetty.xml");
	}
	
	/**
	 * Constructor
	 * 
	 * @param p the port on which to listen
	 * @param jettyCfg the name of the jetty configuration file
	 */
	public WebServer(final int p, final String jettyCfg) {
        super(p);
        webserver = new Server(port);
        XmlConfiguration configuration;
        try {
            URL cfgJtty = ClassLoader.getSystemClassLoader().getResource(jettyCfg);
            if (cfgJtty == null){
                logger.info("Seems like " + jettyCfg + " can't be found/loaded. Let's continue any way.");
                return;
            }
            configuration = new XmlConfiguration(cfgJtty);
            configuration.configure(webserver);
        } catch (Throwable  t) {
            logger.warn("problem configuring jetty", t);
        }
    }

	/**
	 * adds a handler to the server
	 * @param context
	 * @param handler
	 * @throws IllegalStateException if the server has been requested to stop or is stopped 
	 */
	public void addHandler(String context, AbstractHandler handler) {
		HandlerWrapper wrapper = new HandlerWrapper();
		wrapper.setHandler(handler);
		registerHandler(context, new ContextHandler(wrapper, context));
	}

	/**
	 * adds a handler that serves a simple directory containing html pages.
	 * @param context
	 * @param homePath
	 */
	public void addResourceHandler(String context, String homePath) {
        ResourceHandler handler = new ResourceHandler();
        handler.setResourceBase(homePath);
		addHandler(context, handler);
	}
	
	/**
	 * adds a webapp as a handler
	 * @param server the jetty server
	 * @param webappPath the path where the webapp is contained
	 * @param contextPath the context where the webapp will be installed (must start with "/")
	 */
	public void addWebAppHandler(String context, String webappPath) {
        WebAppContext ctxt = new WebAppContext(webappPath, context);
        registerHandler(context, ctxt);
	}

	/**
	 * adds a servlet as a handler
	 * 
	 * right now, it is incompatible with the other types of handlers
	 * @TODO make it work with the other addHandlers
	 * 
	 * @param jettyServer
	 * @param servlet
	 * @param context
	 */
	public void addServletHandler(String context, HttpServlet servlet) {
		ServletHandler servletHandler = new ServletHandler();
		servletHandler.addServletWithMapping(new ServletHolder(servlet), context);
		registerHandler(context, servletHandler);
	}

	synchronized private void registerHandler(String context, AbstractHandler handler) {
        if (RunningState.STOPPING == runningState || RunningState.STOPPED == runningState) {
            throw new IllegalStateException("Server has already been signaled to stop, cannot add handler");
        }
		webserver.addHandler(handler);
		handlers.put(context, handler);
	}
	
	/**
	 * Starts the server in its own thread.
	 */
	@Override protected void startServer() {
		logger.info("WebServer starting on port " + port);
        try {
    		webserver.start();
        } catch (Exception e) {
            logger.fatal(e,e);
            throw new RuntimeException (e);
        }
	}

    public Server getWebServer() {
    	return webserver;
    }

	@Override
	protected Map<String, AbstractHandler> getHandlers() {
		return handlers;
	}

	@Override
	protected void requestStopServer() {
		try {
			webserver.stop();
		} catch (Exception e) {
            logger.fatal(e,e);
            throw new RuntimeException (e);
		}
	}

}

