package com.flaptor.util.web;

import javax.servlet.http.HttpServletRequest;
import com.flaptor.util.web.AbstractAuthorizationFilter;

/**
 * filter for checking logged in users
 */
public class UserAuthorizationFilter extends AbstractAuthorizationFilter {

    @Override
    protected boolean isAuthorized(String destPage, HttpServletRequest request) {
        return (null != request.getSession().getAttribute("user"));
    }
}