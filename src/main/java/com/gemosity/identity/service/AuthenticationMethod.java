package com.gemosity.identity.service;

import com.gemosity.identity.dto.LoginCredentials;
import com.gemosity.identity.dto.OAuthToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthenticationMethod {
    OAuthToken authenticateUser(HttpServletRequest http_request,
                                HttpServletResponse http_response,
                                LoginCredentials loginCredentials,
                                OAuthToken authenticationToken);
}
