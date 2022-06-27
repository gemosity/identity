package com.gemosity.identity.service;

import com.auth0.jwt.JWT;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final SecretLoader secretLoader;

    @Autowired
    public JwtService(SecretLoader secretLoader) {
        this.secretLoader = secretLoader;
    }

    public  Map<String, Claim> verifyToken(String json_auth_str, String signature) {

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

            if(oauth_token.getAccess_token() != null) {
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
                System.out.println("Access Token is NULL " + json_auth_str);
            }
        } else {
            System.out.println("Auth Token missing " + json_auth_str);
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

    public OAuthToken issueToken(CredentialDTO loggedInUser, int validityPeriod) {
        OAuthToken oauthToken = null;

        if(loggedInUser != null) {

            Algorithm algorithm = Algorithm.HMAC256(secretLoader.loadSecret());

            try {
                Calendar expiresAtCal = Calendar.getInstance();

                Date tokenIssuedAt = new Date();
                expiresAtCal.setTime(tokenIssuedAt);
                expiresAtCal.add(Calendar.MINUTE, validityPeriod);
                Date expiresAt = expiresAtCal.getTime();

                String token = JWT.create().withIssuer("Gemosity Ltd")
                        .withExpiresAt(expiresAt)
                        .withIssuedAt(tokenIssuedAt)
                        .withClaim("data", loggedInUser.getUsername())
                        .withClaim("dom", loggedInUser.getDomain())
                        .withClaim("id", loggedInUser.getClientUuid())
                        .sign(algorithm);


                oauthToken = new OAuthToken();
                oauthToken.setAccess_token(token);
                oauthToken.setExpires_in(expiresAt);
                oauthToken.setToken_type("Bearer");
                //oauthToken.setScope(loggedInUser.getRoles());

            } catch (JWTCreationException e) {
                // Invalid signing configuration or could not convert claims
                e.printStackTrace();
            }
        }

        return oauthToken;
    }


}
