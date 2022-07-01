package com.gemosity.identity.service;

import com.gemosity.identity.dto.CredentialDTO;
import com.gemosity.identity.dto.LoginCredentials;
import com.gemosity.identity.dto.OAuthToken;
import com.gemosity.identity.util.PasswordEncoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
public class AuthServiceTests {
    private AuthService authService;

    @Mock
    private CredentialsService credentialsService;

    @Mock
    private JwtAndCookieAuthentication jwtAndCookieAuthentication;

    @BeforeEach
    void setUp() {
        authService = new AuthService(credentialsService, jwtAndCookieAuthentication);
    }

    @Test
    void contextLoads() {
    }

    @Test
    void loginUser() {
        PasswordEncoder encoder = new PasswordEncoder();

        MockHttpServletRequest http_request = new MockHttpServletRequest();
        http_request.setRemoteAddr("127.0.0.1");
        MockHttpServletResponse http_response = new MockHttpServletResponse();
        LoginCredentials loginCredentials = generateLoginCredentials("domain", "username", "password");

        CredentialDTO credentialDTO = new CredentialDTO();
        credentialDTO.setFailedLoginAttempts(0);

        credentialDTO.setPassword(encoder.encode(loginCredentials.getPassword()));
        Optional<CredentialDTO> credentialObj = Optional.of(credentialDTO);

        OAuthToken authenticationToken = new OAuthToken();
        Mockito.when(credentialsService.fetchCredentials(eq("domain"), eq("username"))).thenReturn(credentialObj);
        Mockito.when(jwtAndCookieAuthentication.authenticateUser(any(), any(), any())).thenReturn(authenticationToken);

        OAuthToken oauthToken = authService.loginUser(http_request, http_response, loginCredentials);
        //properties.put("last_successful_login", Long.toString(userCredentials.getLastSuccessfulLogin()));
       // properties.put("last_unsuccessful_login", Long.toString(userCredentials.getLastUnsuccessfulLogin()));
//        oauthToken.getAccess_token();
//        oauthToken.getExpires_in();
//        oauthToken.getProperties();
//        oauthToken.getRefresh_token();
//        oauthToken.getScope()
    }

    @Test
    void tooManyFailedLoginAttempts() {
        PasswordEncoder encoder = new PasswordEncoder();

        MockHttpServletRequest http_request = new MockHttpServletRequest();
        http_request.setRemoteAddr("127.0.0.1");
        MockHttpServletResponse http_response = new MockHttpServletResponse();
        LoginCredentials loginCredentials = generateLoginCredentials("domain", "username", "password");

        CredentialDTO credentialDTO = new CredentialDTO();
        credentialDTO.setFailedLoginAttempts(10);

        credentialDTO.setPassword(encoder.encode(loginCredentials.getPassword()));
        Optional<CredentialDTO> credentialObj = Optional.of(credentialDTO);

        OAuthToken authenticationToken = new OAuthToken();
        Mockito.when(credentialsService.fetchCredentials(eq("domain"), eq("username"))).thenReturn(credentialObj);
        Mockito.when(jwtAndCookieAuthentication.authenticateUser(any(), any(), any())).thenReturn(authenticationToken);

        OAuthToken oauthToken = authService.loginUser(http_request, http_response, loginCredentials);
        Assertions.assertEquals("Account locked.", oauthToken.getProperties().get("error_msg"));
    }

    @Test
    void noMatchForUsernameOrPassword() {
        PasswordEncoder encoder = new PasswordEncoder();

        MockHttpServletRequest http_request = new MockHttpServletRequest();
        http_request.setRemoteAddr("127.0.0.1");
        MockHttpServletResponse http_response = new MockHttpServletResponse();
        LoginCredentials loginCredentials = generateLoginCredentials("domain", "unknownUsername", "password");
        CredentialDTO credentialDTO = new CredentialDTO();
        credentialDTO.setFailedLoginAttempts(0);

        credentialDTO.setPassword(encoder.encode(loginCredentials.getPassword()));
        Optional<CredentialDTO> credentialObj = Optional.of(credentialDTO);

        OAuthToken authenticationToken = new OAuthToken();
        Mockito.when(credentialsService.fetchCredentials(eq("domain"), eq("username"))).thenReturn(credentialObj);
        Mockito.when(jwtAndCookieAuthentication.authenticateUser(any(), any(), any())).thenReturn(authenticationToken);

        OAuthToken oauthToken = authService.loginUser(http_request, http_response, loginCredentials);
        Assertions.assertEquals("No match for username/password combination", oauthToken.getProperties().get("error_msg"));
    }

    private LoginCredentials generateLoginCredentials(String domain, String username, String password) {
        LoginCredentials loginCredentials = new LoginCredentials();
        loginCredentials.setDomain(domain);
        loginCredentials.setUsername(username);
        loginCredentials.setPassword(password);
        return loginCredentials;
    }

}
