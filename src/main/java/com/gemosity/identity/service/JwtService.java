package com.gemosity.identity.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gemosity.identity.dto.CredentialDTO;
import com.gemosity.identity.dto.OAuthToken;
import com.gemosity.identity.util.SecretLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JwtService {
    private static final Logger log = LogManager.getLogger(JwtAndCookieAuthentication.class);

    private final SecretLoader secretLoader;

    @Autowired
    public JwtService(SecretLoader secretLoader) {
        this.secretLoader = secretLoader;
    }

    public String generateIDToken(Map<String, Object> userProfile, String scope) {
        String id_token = null;

        Map<String, String> defaultScopes = new HashMap<>();
        defaultScopes.put("profile", "name, given_name, roles, middle_name, family_name, nickname, picture, website, gender, birthdate, locale, updated_at");
        defaultScopes.put("email", "email, email_verified");

        if (userProfile != null) {

            Algorithm algorithm = Algorithm.HMAC256(secretLoader.loadSecret());

            try {
                Calendar expiresAtCal = Calendar.getInstance();

                Date tokenIssuedAt = new Date();
                expiresAtCal.setTime(tokenIssuedAt);
                expiresAtCal.add(Calendar.HOUR, 10);
                Date expiresAt = expiresAtCal.getTime();
                JWTCreator.Builder id_token_builder = JWT.create().withIssuer("Gemosity Ltd").withExpiresAt(expiresAt).
                        withIssuedAt(tokenIssuedAt);

                String[] scopes = scope.split(",");

                for(String currentScope: scopes) {
                    String scopeClaims = defaultScopes.get(currentScope.trim());
                    String[] scopeClaimsArr = scopeClaims.split(",");

                    for(String claim: scopeClaimsArr) {

                        Object claimContent = userProfile.get(claim.trim());

                        if(claimContent != null) {
                            if (claimContent instanceof String) {
                                id_token_builder.withClaim(claim.trim(), (String) claimContent);
                            } else if (claimContent instanceof Integer) {
                                id_token_builder.withClaim(claim.trim(), (int) claimContent);
                            } else if (claimContent instanceof Long) {
                                id_token_builder.withClaim(claim.trim(), (long) claimContent);
                            } else if (claimContent instanceof ArrayList) {
                                id_token_builder.withClaim(claim.trim(), (ArrayList) claimContent);
                            }  else {
                                log.error("Unsupported claim object type for " + claim.trim() + " - " + claimContent.getClass().getName());
                            }
                        }
                    }
                }

                id_token = id_token_builder.sign(algorithm);
            } catch (JWTCreationException e) {
                // Invalid signing configuration or could not convert claims
                e.printStackTrace();
            }
        } else {
            log.error("Invalid user profile");
        }

        return id_token;
    }

    public OAuthToken issueToken(CredentialDTO loggedInUser, String id_token, int validityPeriod) {
        OAuthToken oauthToken = null;

        if (loggedInUser != null) {

            Algorithm algorithm = Algorithm.HMAC256(secretLoader.loadSecret());

            try {
                Calendar expiresAtCal = Calendar.getInstance();

                Date tokenIssuedAt = new Date();
                expiresAtCal.setTime(tokenIssuedAt);
                expiresAtCal.add(Calendar.MINUTE, validityPeriod);
                Date expiresAt = expiresAtCal.getTime();

                // cms/cmsApi scope for sub (user uuid), dom (domain), id (client uuid), data (username) ?
                // probably no longer require data(username) claim as this can be retrieved from the ID token.
                String token = JWT.create().withIssuer("Gemosity Ltd").withExpiresAt(expiresAt).withIssuedAt(tokenIssuedAt)
                        .withClaim("sub", loggedInUser.getUuid())
                        .withClaim("data", loggedInUser.getUsername())
                        .withClaim("dom", loggedInUser.getDomain())
                        .withClaim("id", loggedInUser.getClientUuid())
                        .sign(algorithm);

                oauthToken = new OAuthToken();
                oauthToken.setAccess_token(token);
                oauthToken.setId_token(id_token);
                oauthToken.setExpires_in(expiresAt);
                oauthToken.setToken_type("Bearer");
                //oauthToken.setScope(loggedInUser.getRoles());

            } catch (JWTCreationException e) {
                // Invalid signing configuration or could not convert claims
                e.printStackTrace();
            }
        } else {
            log.error("User is not logged in");
        }

        return oauthToken;
    }

    public Map<String, Claim> verifyToken(String json_auth_str, String signature) {

        Map<String, Claim> claims = null;

        ObjectMapper mapper = new ObjectMapper();
        OAuthToken oauth_token = null;

        try {
            oauth_token = mapper.readValue(json_auth_str, OAuthToken.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // verify JWT token
        if (oauth_token != null) {

            if (oauth_token.getAccess_token() != null) {
                String[] jwtArr = oauth_token.getAccess_token().split("\\.");
                String realJwt = jwtArr[0] + "." + jwtArr[1] + "." + signature;

                try {
                    DecodedJWT jwt = validateToken(realJwt);
                    claims = jwt.getClaims();

                } catch (JWTVerificationException e) {
                    // Invalid signature, claims, or token has expired.
                    e.printStackTrace();
                }
            } else {
                log.error("Invalid authentication token" + json_auth_str);
            }
        } else {
            log.error("Authentication Token missing " + json_auth_str);
        }

        return claims;
    }

    private DecodedJWT validateToken(String token) {
        DecodedJWT decodedJWT = null;
        Algorithm algorithm = Algorithm.HMAC256(secretLoader.loadSecret());

        try {
            JWTVerifier verifier = JWT.require(algorithm).withIssuer("Gemosity Ltd").build();
            decodedJWT = verifier.verify(token);
        } catch (com.auth0.jwt.exceptions.TokenExpiredException e) {
            e.printStackTrace();
        }

        return decodedJWT;
    }


}
