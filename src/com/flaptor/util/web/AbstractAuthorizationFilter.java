package com.flaptor.util.web;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Generic filter for checking if the user is authorized to see a web page.
 * You should use it in conjunction with the url-pattern in web.xml
 * 
 * init params: 
 * 
 * failPage - the page (jsp, vm, do, etc) to show to the user if he is not authorized
 * restrictedPages - pages that require login; any other page will be accessible without login.
 * openPages - if restrictedPages is not defined, only the openPages will not need login.
 * 
 * @author Martin Massera
 *
 */
abstract public class AbstractAuthorizationFilter implements Filter {
    private Set<String> openPages = new HashSet<String>(); // Pages that need no login   
    private Set<String> restrictedPages = new HashSet<String>(); // Pages that need user login
    private boolean usingRestrictedPages = false;
    private boolean usingOpenPages = false;
    private String failPage;

    public void destroy() {
    }

    public void init(FilterConfig config) throws ServletException {
        String open = config.getInitParameter("openPages");
        String user = config.getInitParameter("restrictedPages");
        if (user != null) {
       		usingRestrictedPages = true;
        	restrictedPages.addAll(Arrays.asList(user.split(":")));
        }
        if (open != null) {
        	usingOpenPages = true;
        	openPages.addAll(Arrays.asList(open.split(":")));        
        }
        failPage = config.getInitParameter("failPage");
    }

    /**
     * Determine if authorization is required. If so, request authorization and proceed if ok.
     * Otherwise, present error page.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) request;
        String destPage = r.getRequestURI().replaceFirst(r.getContextPath(),"");
        boolean authorizationNeeded = true; // if nothing specified, authorization always needed
        if (usingRestrictedPages) { 		// restricted pages need authorization, if defined.
        	authorizationNeeded = restrictedPages.contains(destPage);
        } else if (usingOpenPages) {		// if no user pages defined, only openPages can skip authorization.
        	authorizationNeeded = !openPages.contains(destPage);
        }
        if (!authorizationNeeded || isAuthorized(destPage, r)) {
            chain.doFilter(request, response);
        } else {
        	addReference(r);
            r.getRequestDispatcher(failPage).forward(request, response);
        }
    }
    
    /**
     * Add a reference to the destination page so the failPage 
     * can go there after displaying.
     * @param request
     */
    protected void addReference(HttpServletRequest request) {
        String dest = request.getRequestURI().replaceFirst(request.getContextPath(), "");
        if (request.getParameterMap().isEmpty())
        	request.setAttribute("dst", dest);
        else {
        	request.setAttribute("dst", dest + "?" + request.getQueryString());
        }
    }

    /**
     * Determine if the user is authorized to access the requested page.
     * @param destPage the page requested, without context path.
     * @param request the request.
     * @return true iff the user is authorized to access the page.
     */
    abstract protected boolean isAuthorized(String destPage, HttpServletRequest request);
    
}
