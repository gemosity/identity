package com.gemosity.identity.service;

import com.gemosity.identity.dto.CredentialDTO;
import com.gemosity.identity.dto.LoginCredentials;
import com.gemosity.identity.dto.OAuthToken;
import com.gemosity.identity.util.AuthTokenWrapper;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class JwtAndCookieAuthentication implements AuthenticationMethod {
    private static final int JWT_TOKEN_VALIDITY_IN_MINS = 17;

    private static final int JWT_HEADER = 0;
    private static final int JWT_PAYLOAD = 1;
    private static final int JWT_SIGNATURE = 2;

    private AuthTokenWrapper wrapper;

    private JwtService jwtService;

    public JwtAndCookieAuthentication(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public OAuthToken authenticateUser(HttpServletRequest http_request,
                                       HttpServletResponse http_response,
                                       CredentialDTO loggedInUser) {

        OAuthToken authenticationToken = jwtService.issueToken(loggedInUser, JWT_TOKEN_VALIDITY_IN_MINS);

        AuthTokenWrapper wrapper = new AuthTokenWrapper();
        String[] token_parts = authenticationToken.getAccess_token().split("\\.");

        // Signature HTTP-only cookie (stores valid JWT signature)
        Cookie signatureCookie = generateCookie("sessionId",
                                                token_parts[JWT_SIGNATURE],
                                                JWT_TOKEN_VALIDITY_IN_MINS, true);
        http_response.addCookie(signatureCookie);

        // Domain cookie
        Cookie domainCookie = generateCookie("domain", loggedInUser.getDomain(),1000, false);
        http_response.addCookie(domainCookie);

        // Corrupt JWT signature (good copy is stored in HTTP only cookie)
        String tokenWithFakeSignature = replaceAccessTokenSignature(token_parts);
        authenticationToken.setAccess_token(tokenWithFakeSignature);

        wrapper.setOauthToken(authenticationToken);

       return authenticationToken;

    }

    private String replaceAccessTokenSignature(String[] token_parts) {

        // Generate a fake signature for the JWT
        byte[] fakeSignature1 = new byte[21];

        try {
            // Change from SecureRandom.getInstanceStrong().nextBytes() as can block for very long time !!!!
            SecureRandom.getInstance("NativePRNGNonBlocking").nextBytes(fakeSignature1);
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] encodedFakeSignature = Base64.getEncoder().encode(fakeSignature1);
        String tokenWithFakeSignature = token_parts[JWT_HEADER] + "." + token_parts[JWT_PAYLOAD] + "." + new String(encodedFakeSignature);

        return tokenWithFakeSignature;
    }

    private Cookie generateCookie(String cookieName, String cookieValue, int maxAgeMinutes, boolean httpOnly) {
        Cookie generatedCookie = new Cookie(cookieName, cookieValue);
        generatedCookie.setHttpOnly(httpOnly);
        generatedCookie.setPath("/");
        generatedCookie.setMaxAge(maxAgeMinutes * 60);
        generatedCookie.setSecure(false);
        return generatedCookie;
    }
}
