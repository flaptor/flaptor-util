package com.flaptor.util.remote;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.xmlrpc.WebServer;
import org.apache.xmlrpc.XmlRpcServer;
import org.mortbay.jetty.handler.AbstractHandler;

import com.flaptor.util.ClassUtil;
import com.flaptor.util.FileUtil;

/**
 * This class implements a generic server that listens on a port for xmlrpc
 * indexing requests and forwards them to a handler object.
 */
public class XmlrpcServer extends AServer {
	
	public static final String DEFAULT_CONTEXT = "$default";

	private static Logger logger = Logger.getLogger(com.flaptor.util.Execute.whoAmI());

	private final Map<String, Object> handlers = new HashMap<String, Object>(); 
	private final WebServer webserver;

	/**
	 * Runs the server from the command line, exporting the handler class at the default context
	 * 
	 * @param args
	 *            First: Handler class. Must have a default constructor.
     *            Second: port
	 */
	public static void main(final String[] args) {
		if (args.length != 2) {
			System.err.println("usage: Server [handler class] [port]");
			System.exit(-1);
		}

       String log4jConfigPath = FileUtil.getFilePathFromClasspath("log4j.properties");
       if (null != log4jConfigPath ) {
   		PropertyConfigurator.configureAndWatch(log4jConfigPath);
       } else {
           logger.warn("log4j.properties not found on classpath! Logging configuration will not be reloaded.");
       }

		try {
			XmlrpcServer server = new XmlrpcServer(Integer.parseInt(args[1]));
			server.addHandler(null, ClassUtil.instance(args[0]));
			server.start();
		} catch (Throwable e) {
			System.err.println(e);
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param context the context where the service is exported, if null uses the default context
	 * @param h the objet to handle the requests.
	 * @param p the port on which to listen
	 */
	public XmlrpcServer(final int p) {
	    super(p);
		webserver = new WebServer(port);
		webserver.setParanoid(false);
	}
	
	/**
	 * adds a handler to the server
	 * @param context
	 * @param handler
	 * @throws IllegalStateException if the server has been requested to stop or is stopped 
	 */
	public void addHandler(String context, Object handler) {
		if (stopRequested) throw new IllegalStateException("Server has already been signaled to stop, cannot add handler");
		
		if (context == null) context = DEFAULT_CONTEXT;
		webserver.addHandler(context, handler);
		handlers.put(context, handler);
	}
	
	/**
	 * Starts the server in its own thread.
	 */
	@Override 
	protected void startServer() {
		logger.info("XmlrpcServer starting on port " + port);
		webserver.start();
	}
	
	@Override
	protected Map<String, ? extends Object> getHandlers() {
		return handlers;
	}

	@Override
	protected boolean isStoppedServer() {
		return stopRequested;
	}

	@Override
	protected void requestStopServer() {
		webserver.shutdown();
	}
}
