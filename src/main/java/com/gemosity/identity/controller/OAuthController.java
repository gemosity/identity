package com.gemosity.identity.controller;

import com.gemosity.identity.dto.OAuthToken;
import com.gemosity.identity.dto.TokenRequest;
import com.gemosity.identity.service.AuthService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
public class OAuthController {

    private final AuthService authService;

    public OAuthController(AuthService authService) {
        this.authService = authService;
    }

    // Request accessToken, refreshToken
    @PostMapping("/oauth/token")
    public OAuthToken requestToken(@CookieValue(value = "sessionId") String signature,
                                   @RequestHeader(value="Authorization") String authToken,
                                   @RequestBody TokenRequest tokenRequest,
                                   HttpServletResponse http_response) {
        return authService.requestToken(tokenRequest, authToken, signature, http_response);
    }

    @GetMapping("/authorize")
    public void authorizeClient(HttpServletResponse http_response) {
        OAuthToken oauthToken = authService.authorizeClient(null, http_response);

        // Redirects to Gemosity Identity Login page to allow user to enter credentials
        // - perhaps could have a special console login for access via ssh?
        // - might also redirect to social media login page if that login type is used
    }
}
