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

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.xmlrpc.WebServer;

import com.flaptor.util.ClassUtil;
import com.flaptor.util.Execute;
import com.flaptor.util.FileUtil;

/**
 * This class implements a generic server that listens on a port for xmlrpc
 * indexing requests and forwards them to a handler object.
 * 
 * @author Martin Massera
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
    public XmlrpcServer(int p) {
        super(p);
        webserver = new WebServer(port);
        webserver.setParanoid(false);
    }

    /**
     * Shortcut constructor that creates and starts a xmlrpc server with the given handler 
     * @param p
     * @param context
     * @param handler
     */
    public XmlrpcServer(int p, String context, Object handler) {
        this(p);
        addHandler(context, handler);
        start();
    }

    /**
     * adds a handler to the server
     * @param context
     * @param handler
     * @throws IllegalStateException if the server has been requested to stop or is stopped 
     */
    public void addHandler(String context, Object handler) {
        if (RunningState.STOPPING == runningState || RunningState.STOPPED == runningState) {
            throw new IllegalStateException("Server has already been signaled to stop, cannot add handler");
        }
        if (context == null) context = DEFAULT_CONTEXT;
        webserver.addHandler(context, handler);
        handlers.put(context, handler);
    }

    /**
     * adds an ip to the list of accepted ips. If addAcceptedClient or addRejectedClient are never called,
     * the server accepts every connection. Else it accept only the ones that are in the accepted list
     * and not in the rejected list
     *  
     * @param ip an ip, can contain wildcards as in 192.168.*.*
     */
    public void addAcceptedClient(String ip) {
        webserver.setParanoid(true);
        webserver.acceptClient(ip);
    }

    /**
     * adds an ip to the list of rejected ips. If addAcceptedClient or addRejectClient are never called,
     * the server accepts every connection. Else it accept only the ones that are in the accepted list
     * and not in the rejected list
     *  
     * @param ip an ip, can contain wildcards as in 192.168.*.*
     */
    public void addDeniedClient(String ip) {
        webserver.setParanoid(true);
        webserver.denyClient(ip);
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
    protected void requestStopServer() {
        webserver.shutdown();
        //This is very, very ugly, but the damn apache webserver does not implemente a blocking shutdown
        //nor it does implement a way of knowing when it has really finished the shutdown sequence.
        logger.info("requestStopServer: shutting down web server... this may take a while.");
        final long endtime = System.currentTimeMillis() + 15000;
        while ( System.currentTimeMillis() < endtime) {
            ServerSocket s = null;
            try {
                s = new ServerSocket(port);
                break;
            } catch (IOException e) {
            } finally {
                Execute.close(s);
            }
        }
        if (System.currentTimeMillis() >= endtime) {
            logger.error("requestStopServer: the webserver never freed up it's port.");
        }
    }

}

