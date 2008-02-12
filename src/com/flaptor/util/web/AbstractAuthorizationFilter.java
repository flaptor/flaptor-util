package com.flaptor.util.web;

import java.io.IOException;
import java.util.Arrays;
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
 * errorPage - the page (jsp, vm, etc) to show to the user if he is not authorized
 * okPages - pages that do not need authentication (optional)
 * 
 * @author Martin Massera
 *
 */
abstract public class AbstractAuthorizationFilter implements Filter {
    private String errorPage;
    private Set<String> okPages = new HashSet<String>();   

    public void destroy() {
    }

    public void init(FilterConfig config) throws ServletException {
        errorPage = config.getInitParameter("errorPage");
        String oks = config.getInitParameter("okPages");
        if (oks != null) okPages.addAll(Arrays.asList(oks.split(":")));        
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) request;
        String destPage = r.getRequestURI().replaceFirst(r.getContextPath(),"");

        if (isAuthorized(destPage, r) || okPages.contains(destPage)) {
            chain.doFilter(request, response);
        } else {
            r.getRequestDispatcher(errorPage).forward(request, response);
        }
    }
    
    /**
     * @param destPage the page requested, without context path
     * @param request the request
     * @return true iff the user is authorized to access the page
     */
    abstract protected boolean isAuthorized(String destPage, HttpServletRequest request);
}
