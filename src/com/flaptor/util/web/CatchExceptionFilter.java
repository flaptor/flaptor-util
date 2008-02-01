package com.flaptor.util.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

import com.flaptor.util.Execute;

/**
 * Filter that catches uncaught throwbles and redirects to an error page.
 * The error page is configured through the init params,
 * with the parameter errorPage
 * 
 * @author Martin Massera
 */
public class CatchExceptionFilter implements Filter
{
    private static final Logger logger = Logger.getLogger(Execute.whoAmI());
    private String errorPage;
    
    public void destroy(){}

    public void init(FilterConfig config) throws ServletException {
        errorPage = config.getInitParameter("errorPage");
    }
    
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        try
        {
            chain.doFilter(request, response);
        }
        catch (Throwable t)
        {
            logger.warn("Exception caught", t);
            request.setAttribute("throwable", t);
            request.getRequestDispatcher(errorPage).forward(request, response);
        }
    }   
}
