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
import javax.servlet.http.HttpSession;

import com.flaptor.util.StringUtil;

/**
 * Generic filter for checking if the user is logged in. 
 * If he is not he is shown a login page. In this login page a 
 * parameter "dst" is added with the original destination
 * 
 * init params: 
 * 
 * loginPage - the page (jsp, vm, etc) to show to the user if he is not authorized
 * okPages - pages that do not need authentication (optional)
 * 
 * @author Martin Massera
 *
 */
abstract public class AbstractLoginAuthorizationFilter implements Filter {

    private String loginPage;
    private Set<String> okPages = new HashSet<String>();

    public void destroy() {
    }

    public void init(FilterConfig config) throws ServletException {
        loginPage = config.getInitParameter("loginPage");
        String oks = config.getInitParameter("okPages");
        if (oks != null) okPages.addAll(Arrays.asList(oks.split(":")));        
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) request;
        String destPage = r.getRequestURI().replaceFirst(r.getContextPath(),"");

        if (okPages.contains(destPage) || isLogged(r.getSession())) {
            chain.doFilter(request, response);
        } else {
            String dest = r.getRequestURI().replaceFirst(r.getContextPath(), "");

            if (r.getParameterMap().isEmpty())
                r.setAttribute("dst", dest);
            else {
                String params = "";
                Enumeration e = r.getParameterNames();
                for (; e.hasMoreElements();) {
                    String par = e.nextElement().toString();
                    params = par + "=" + r.getParameter(par) + "&" + params;
                }
                r.setAttribute("dst", dest + "?" + params.substring(0, params.length() - 1));
            }
            r.getRequestDispatcher(loginPage).forward(request, response);
        }
    }
    
    /**
     * @param session the http session
     * @return true iff the user is logged in
     */
    abstract protected boolean isLogged(HttpSession session);
}