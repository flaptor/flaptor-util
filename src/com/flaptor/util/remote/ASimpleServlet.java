package com.flaptor.util.remote;


import javax.servlet.http.HttpServlet;
import org.apache.log4j.Logger;
import com.flaptor.util.Execute;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.ServletMapping;

/**
 * Simple Servlet that doesn't require a directory structure or a web.xml file.
 * Must be extended to implement the init and the service methods.
 */
public abstract class ASimpleServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(Execute.whoAmI());
    
    
    /**
     * Start the servlet
     * @param servletClass the class that extends ASimpleServlet.
     * @param port the port the servlet will be listenting on.
     * @param contextPath the context path where the servlet will attend.
     * @param pathSpec a regular expression selecting the paths the servlet will attend.
     * @throws java.lang.Exception may be thrown when starting the server.
     */
     public void startServlet(Class servletClass, int port, String contextPath, String pathSpec) throws Exception {
        Server server = new Server(port);
        Context context = new Context();
        context.setContextPath(contextPath);
        server.addHandler(context);
        ServletHandler handler = context.getServletHandler();
        ServletHolder holder = new ServletHolder();
        holder.setClassName(servletClass.getName());
        holder.setName(servletClass.getSimpleName());
        handler.addServlet(holder);
        ServletMapping mapping = new ServletMapping();
        mapping.setServletName(servletClass.getSimpleName());
        mapping.setPathSpec(pathSpec);
        handler.addServletMapping(mapping);
        server.start();
     }
    
}
