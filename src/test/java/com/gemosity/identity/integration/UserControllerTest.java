package com.gemosity.identity.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gemosity.identity.dto.LoginCredentials;
import com.gemosity.identity.dto.OAuthToken;
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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    private ObjectMapper objectMapper;

    @Value("${login.username}")
    private String username;

    @Value("${login.password}")
    private String password;

    @Value("${login.domain}")
    private String domain;

    @LocalServerPort
    private int randomPort;

    public UserControllerTest() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void oidcUserInfoEndpoint() throws URISyntaxException {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:" + randomPort)
                .build();

        MultiValueMap<String, ResponseCookie> authCookies = new LinkedMultiValueMap<>();

        String loginCredentialsJson = generateLoginCredentials(username, password, domain);

        OAuthToken oAuthToken = webClient.post()
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

        String idTokenJwt = webClient.get()
                .uri(new URI("http://localhost:" + randomPort + "/oidc/userinfo/jwt"))
                .header("Authorization", "Bearer " + generateOAuthTokenJson(oAuthToken))
                .cookie("sessionId", authCookies.get("sessionId").get(0).getValue())
                .cookie("domain", authCookies.get("domain").get(0).getValue())
                .accept(MediaType.valueOf("application/json"))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println(oAuthToken.getId_token());
        System.out.println("idTokenJwt: " + idTokenJwt);
        Assertions.assertEquals(true, idTokenJwt.contains(oAuthToken.getId_token()));

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
