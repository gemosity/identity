package com.gemosity.identity.service;

import com.gemosity.identity.dto.LoginCredentials;
import com.gemosity.identity.dto.OAuthToken;
import com.gemosity.identity.dto.TokenRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IAuthService {
    OAuthToken loginUser(HttpServletRequest http_request,
                                HttpServletResponse http_response,
                                LoginCredentials loginCredentials);
    void logout(HttpServletResponse http_response);

    OAuthToken refreshToken(String authToken, String signature, HttpServletResponse http_response);

    OAuthToken requestToken(TokenRequest tokenRequest, String authToken, String signature, HttpServletResponse http_response);
}
