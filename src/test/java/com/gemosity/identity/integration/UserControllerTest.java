package com.gemosity.identity.integration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gemosity.identity.dto.LoginCredentials;
import com.gemosity.identity.dto.OAuthToken;
import com.gemosity.identity.dto.UserProfile;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    private ObjectMapper objectMapper;

    @Value("${login.username}")
    private String username;

    @Value("${login.password}")
    private String password;

    @Value("${login.domain}")
    private String domain;

    private WebClient webClient;
    private MultiValueMap<String, ResponseCookie> authCookies = new LinkedMultiValueMap<>();
    private OAuthToken oAuthToken;

    @LocalServerPort
    private int randomPort;

    public UserControllerTest() {
        objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void authenticate() throws URISyntaxException {

        String loginCredentialsJson = generateLoginCredentials(username, password, domain);

        webClient = WebClient.builder()
                .baseUrl("http://localhost:" + randomPort)
                .build();


        oAuthToken = webClient.post()
                .uri(new URI("http://localhost:" + randomPort + "/api/oauth/login"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(loginCredentialsJson))
                .exchangeToMono(response -> {
                    MultiValueMap<String, ResponseCookie> cookies = response.cookies();
                    for (var cookie : cookies.entrySet()) {
                        authCookies.put(cookie.getKey(), cookie.getValue());
                    }

                    return response.bodyToMono(OAuthToken.class);
                })
                .block();
    }

    @Test
    public void oidcUserInfoEndpointAsJWT() throws URISyntaxException {

        String idTokenJwt = webClient.get()
                .uri(new URI("http://localhost:" + randomPort + "/oidc/userinfo"))
                .header("Authorization", "Bearer " + generateOAuthTokenJson(oAuthToken))
                .header("Content-Type", "application/jwt")
                .cookie("sessionId", authCookies.get("sessionId").get(0).getValue())
                .cookie("domain", authCookies.get("domain").get(0).getValue())
                .accept(MediaType.valueOf("application/json"))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            JSONObject jsonObject = new JSONObject(idTokenJwt);
            String idTokenAsJwt = jsonObject.get("id").toString();
            DecodedJWT decodedJWT = JWT.decode(idTokenAsJwt);
            DecodedJWT oauthIdTokenJWT = JWT.decode(oAuthToken.getId_token());
            Map<String, Claim> claims = decodedJWT.getClaims();
            Map<String, Claim> oauthIDTokenClaims = oauthIdTokenJWT.getClaims();

            Assertions.assertEquals(claims.get("given_name").asString(), oauthIDTokenClaims.get("given_name").asString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void oidcUserInfoEndpointAsJson() throws URISyntaxException {

        UserProfile userProfile = webClient.get()
                .uri(new URI("http://localhost:" + randomPort + "/oidc/userinfo"))
                .header("Authorization", "Bearer " + generateOAuthTokenJson(oAuthToken))
                .header("Content-Type", "application/json")
                .cookie("sessionId", authCookies.get("sessionId").get(0).getValue())
                .cookie("domain", authCookies.get("domain").get(0).getValue())
                .accept(MediaType.valueOf("application/json"))
                .retrieve()
                .bodyToMono(UserProfile.class)
                .block();


        DecodedJWT oauthIdTokenJWT = JWT.decode(oAuthToken.getId_token());
        Map<String, Claim> oauthIDTokenClaims = oauthIdTokenJWT.getClaims();

        Assertions.assertEquals(userProfile.getGiven_name(), oauthIDTokenClaims.get("given_name").asString());

    }

    private String generateLoginCredentials(String username, String password, String domain) {
        String json_body = null;

        LoginCredentials loginCredentials = new LoginCredentials();
        loginCredentials.setPassword(password);
        loginCredentials.setUsername(username);
        loginCredentials.setDomain(domain);

        try {
            json_body = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(loginCredentials);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json_body;
    }

    private String generateOAuthTokenJson(OAuthToken oAuthToken) {
        String json_body = null;

        try {
            json_body = objectMapper.writeValueAsString(oAuthToken);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json_body;
    }
}
