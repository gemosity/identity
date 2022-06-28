package com.gemosity.identity.service;

import com.auth0.jwt.interfaces.Claim;
import com.gemosity.identity.dto.CredentialDTO;
import com.gemosity.identity.dto.OAuthToken;
import com.gemosity.identity.util.AuthTokenWrapper;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

@Component
public class JwtAndCookieAuthentication implements AuthenticationMethod {
    private static final int JWT_TOKEN_VALIDITY_IN_MINS = 17;

    private static final int JWT_HEADER = 0;
    private static final int JWT_PAYLOAD = 1;
    private static final int JWT_SIGNATURE = 2;

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

    @Override
    public OAuthToken refreshUserAuthentication(HttpServletResponse http_response,
                                                CredentialDTO specifiedUser) {

        OAuthToken oauthToken = jwtService.issueToken(specifiedUser, JWT_TOKEN_VALIDITY_IN_MINS);

        if (oauthToken == null) {
            System.out.println("Error generating token");
        } else {
            //OAuthToken refreshToken = jwtService.issueToken(specifiedUser, 1000);

            String[] token_parts = oauthToken.getAccess_token().split("\\.");
            Cookie signatureCookie = generateCookie("sessionId", token_parts[JWT_SIGNATURE], JWT_TOKEN_VALIDITY_IN_MINS, true);
            http_response.addCookie(signatureCookie);

            Cookie domainCookie = generateCookie("domain", specifiedUser.getDomain(),1000, false);
            http_response.addCookie(domainCookie);

            String tokenWithFakeSignature = replaceAccessTokenSignature(token_parts);
            oauthToken.setAccess_token(tokenWithFakeSignature);
        }

        return oauthToken;
    }

    @Override
    public Map<String, Claim> verifyAuthentication(String json_auth_str, String signature) {
        return jwtService.verifyToken(json_auth_str, signature);
    }

    @Override
    public void logout(HttpServletResponse http_response) {
        Cookie signatureCookie = generateCookie("sessionId", null, 0, true);
        Cookie domainCookie = generateCookie("domain", null, 0, false);

        http_response.addCookie(signatureCookie);
        http_response.addCookie(domainCookie);
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
