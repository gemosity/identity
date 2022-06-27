package com.gemosity.identity.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import com.gemosity.identity.dto.CredentialDTO;
import com.gemosity.identity.dto.OAuthToken;
import com.gemosity.identity.util.SecretLoader;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@SpringBootTest
public class AuthServiceTests {
    private AuthService authService;

    @Mock
    private SecretLoader secretLoader;

    @BeforeEach
    void setUp() {
        authService = new AuthService(secretLoader);
    }

    @Test
    void contextLoads() {
    }

    @Test
    void issueToken() {
        String secret = "signingSecret";
        byte[] secretArr= secret.getBytes();
        Mockito.when(secretLoader.loadSecret()).thenReturn(secretArr);
        Calendar expiresAtCal = Calendar.getInstance();

        int tokenValidForMins = 5;
        CredentialDTO userCredentials = new CredentialDTO();
        userCredentials.setUsername("username");
        userCredentials.setDomain("default");
        userCredentials.setClientUuid("clientUuid");
        OAuthToken oAuthToken = authService.issueToken(userCredentials, tokenValidForMins);

        Date tokenIssuedAt = new Date();
        expiresAtCal.setTime(tokenIssuedAt);
        expiresAtCal.add(Calendar.MINUTE, tokenValidForMins+1);
        Date expiresBefore = expiresAtCal.getTime();

        expiresAtCal.setTime(tokenIssuedAt);
        expiresAtCal.add(Calendar.MINUTE, tokenValidForMins-1);
        Date expiresAfter = expiresAtCal.getTime();

        Assertions.assertEquals("Bearer", oAuthToken.getToken_type());
        Assertions.assertEquals(true, expiresAfter.before( oAuthToken.getExpires_in()));
        Assertions.assertEquals(true, expiresBefore.after( oAuthToken.getExpires_in()));
    }

    @Test
    void issueTokenWithNullCredentials() {
        int tokenValidForMins = 5;
        OAuthToken oAuthToken = authService.issueToken(null, tokenValidForMins);

        Assertions.assertEquals(null, oAuthToken);
    }

    @Test
    void verifyToken() {

        String secret = "signingSecret";
        byte[] secretArr= secret.getBytes();
        Mockito.when(secretLoader.loadSecret()).thenReturn(secretArr);

        Algorithm signingAlgorithm = Algorithm.HMAC256(secret);

        Calendar expiresAtCal = Calendar.getInstance();
        Date tokenIssuedAt = new Date();

        expiresAtCal.setTime(tokenIssuedAt);
        expiresAtCal.add(Calendar.MINUTE, 5);
        Date expiresAt = expiresAtCal.getTime();

        String token = JWT.create().withIssuer("Gemosity Ltd")
                .withExpiresAt(expiresAt)
                .withIssuedAt(tokenIssuedAt)
                .withClaim("data", "username")
                .withClaim("dom", "domain")
                .withClaim("id", "clientUuid")
                .sign(signingAlgorithm);

        String[] jwtArr = token.split("\\.");
        String signature = jwtArr[2];

        String oauthTokenAsJson = constructOAuthToken(token);
        Map<String, Claim> claims = authService.verifyToken(oauthTokenAsJson,signature);

        Assertions.assertNotEquals(null, claims);

    }

    @Test
    void verifyTamperedWithToken() {

        String secret = "signingSecret";
        byte[] secretArr= secret.getBytes();
        Mockito.when(secretLoader.loadSecret()).thenReturn(secretArr);

        Algorithm signingAlgorithm = Algorithm.HMAC256(secret);

        Calendar expiresAtCal = Calendar.getInstance();
        Date tokenIssuedAt = new Date();

        expiresAtCal.setTime(tokenIssuedAt);
        expiresAtCal.add(Calendar.MINUTE, 5);
        Date expiresAt = expiresAtCal.getTime();

        String token = JWT.create().withIssuer("Gemosity Ltd")
                .withExpiresAt(expiresAt)
                .withIssuedAt(tokenIssuedAt)
                .withClaim("data", "username")
                .withClaim("dom", "domain")
                .withClaim("id", "clientUuid")
                .sign(signingAlgorithm);

        String oauthTokenAsJson = constructOAuthToken(token);
        Map<String, Claim> claims = authService.verifyToken(oauthTokenAsJson, "badSignature");

        Assertions.assertEquals(null, claims);

    }

    private String constructOAuthToken(String jwtToken) {
        // , "properties": []
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"access_token\": \"");
        stringBuilder.append(jwtToken);
        stringBuilder.append("\", \"token_type\": \"Bearer\", \"expires_in\": 0, \"scope\": \"admin\"}");
        return stringBuilder.toString();
    }
}
