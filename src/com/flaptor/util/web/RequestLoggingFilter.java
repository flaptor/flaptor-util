package com.flaptor.util.web;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.flaptor.util.Execute;

/**
 * filter that logs requests and exceptions. Also asigns a unique request number to 
 * the request (as attribute unique.request.number) so it can be tracked throughout the
 * whole request life
 * 
 * @author Martin Massera
 */
public class RequestLoggingFilter implements Filter
{
    private static final Logger logger = Logger.getLogger(Execute.whoAmI());

    private static AtomicLong requests = new AtomicLong(0);
	public static long getRequests()
	{
		return requests.get();
	}
	
	public void destroy(){}

	public void init(FilterConfig arg0) throws ServletException	{}
	
	public void doFilter(ServletRequest arg0, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest request = (HttpServletRequest) arg0;
		long reqId = requests.incrementAndGet();
        request.setAttribute("unique.request.number", reqId);
		
		logger.info("request "+ reqId + ": " + request.getRequestURL());
		try
		{
			chain.doFilter(request, response);
	        logger.info("request "+ reqId + ": finished ok");
		}
		catch (ServletException e)
		{
			throwable(e.getRootCause(), request, reqId);
			throw e;
		}
		catch (IOException e)
		{
			throwable(e, request, reqId);
			throw e;
		}
		catch (RuntimeException e)
		{
			throwable(e, request, reqId);
			throw e;			
		}
		catch (Error e)
		{
			throwable(e, request, reqId);
			throw e;
		}
	}
    private void throwable(Throwable t, HttpServletRequest request, long reqId)
    {
        logger.error("Exception in request " + reqId + ": " + request.getRequestURL(), t);
    }
}
