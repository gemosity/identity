package com.gemosity.identity.service;

import com.gemosity.identity.dto.CredentialDTO;
import com.gemosity.identity.dto.OAuthToken;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public interface AuthenticationMethod {
    OAuthToken authenticateUser(HttpServletRequest http_request,
                                HttpServletResponse http_response,
                                CredentialDTO loginCredentials);
}
