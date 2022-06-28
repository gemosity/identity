package com.gemosity.identity.controller;

import com.gemosity.identity.dto.LoginCredentials;
import com.gemosity.identity.dto.OAuthToken;
import com.gemosity.identity.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class OAuthController {

    private final AuthService authService;

    public OAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/api/oauth/login")
    public OAuthToken login(HttpServletResponse http_response, HttpServletRequest http_request,
                            @RequestBody LoginCredentials login_credentials) {

        OAuthToken token = authService.loginUser(http_request, http_response, login_credentials);
        return token;
    }

    @PostMapping("/api/oauth/logout")
    public void logout(HttpServletResponse http_response) {
        authService.logout(http_response);
    }

}
