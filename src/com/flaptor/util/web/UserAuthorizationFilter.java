package com.flaptor.util.web;

import javax.servlet.http.HttpServletRequest;

/**
 * filter for checking logged in users
 */
public class UserAuthorizationFilter extends AbstractAuthorizationFilter {

    @Override
    protected boolean isAuthorized(String destPage, HttpServletRequest request) {
        return (null != request.getSession().getAttribute("user"));
    }
}