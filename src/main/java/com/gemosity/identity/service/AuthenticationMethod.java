package com.gemosity.identity.service;

import com.auth0.jwt.interfaces.Claim;
import com.gemosity.identity.dto.CredentialDTO;
import com.gemosity.identity.dto.OAuthToken;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public interface AuthenticationMethod {
    OAuthToken authenticateUser(HttpServletRequest http_request,
                                HttpServletResponse http_response,
                                CredentialDTO loginCredentials,
                                String id_token);

    OAuthToken refreshUserAuthentication(HttpServletResponse http_response,
                                         CredentialDTO specifiedUser);

    Map<String, Claim> verifyAuthentication(String json_auth_str, String signature);

    void logout(HttpServletResponse http_response);
}
