package com.gemosity.identity.service;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
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

import java.util.*;

import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
public class AuthServiceTests {
    private AuthService authService;

    @Mock
    private CredentialsService credentialsService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private JwtAndCookieAuthentication jwtAndCookieAuthentication;

    @Mock
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(credentialsService, userService, jwtAndCookieAuthentication, jwtService);
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
        credentialDTO.setLastSuccessfulLogin(100);
        credentialDTO.setLastUnsuccessfulLogin(200);
        credentialDTO.setPassword(encoder.encode(loginCredentials.getPassword()));
        Optional<CredentialDTO> credentialObj = Optional.of(credentialDTO);

        OAuthToken authenticationToken = new OAuthToken();
        Mockito.when(credentialsService.fetchCredentials(eq("domain"), eq("username"))).thenReturn(credentialObj);
        Mockito.when(jwtAndCookieAuthentication.authenticateUser(any(), any(), any(), any())).thenReturn(authenticationToken);

        OAuthToken oauthToken = authService.loginUser(http_request, http_response, loginCredentials);

        Assertions.assertEquals("100", oauthToken.getProperties().get("last_successful_login"));
        Assertions.assertEquals("200", oauthToken.getProperties().get("last_unsuccessful_login"));

//        oauthToken.getAccess_token();
//        oauthToken.getExpires_in();
//        oauthToken.getProperties();
//        oauthToken.getRefresh_token();
//        oauthToken.getScope()
    }

    @Test
    void tooManyFailedLoginAttempts() {
        PasswordEncoder encoder = new PasswordEncoder();
        LoginCredentials loginCredentials = generateLoginCredentials("domain",
                "username",
                "password");

        MockHttpServletRequest http_request = new MockHttpServletRequest();
        http_request.setRemoteAddr("127.0.0.1");
        MockHttpServletResponse http_response = new MockHttpServletResponse();

        CredentialDTO credentialDTO = new CredentialDTO();
        credentialDTO.setFailedLoginAttempts(10);

        credentialDTO.setPassword(encoder.encode(loginCredentials.getPassword()));
        Optional<CredentialDTO> credentialObj = Optional.of(credentialDTO);

        OAuthToken authenticationToken = new OAuthToken();
        Mockito.when(credentialsService.fetchCredentials(eq("domain"), eq("username"))).thenReturn(credentialObj);
        Mockito.when(jwtAndCookieAuthentication.authenticateUser(any(), any(), any(), any())).thenReturn(authenticationToken);

        OAuthToken oauthToken = authService.loginUser(http_request, http_response, loginCredentials);
        Assertions.assertEquals("Account locked.", oauthToken.getProperties().get("error_msg"));
    }

    @Test
    void noMatchForUsernameOrPassword() {
        PasswordEncoder encoder = new PasswordEncoder();
        LoginCredentials loginCredentials = generateLoginCredentials("domain",
                "unknownUsername",
                "password");

        MockHttpServletRequest http_request = new MockHttpServletRequest();
        http_request.setRemoteAddr("127.0.0.1");
        MockHttpServletResponse http_response = new MockHttpServletResponse();
        CredentialDTO credentialDTO = new CredentialDTO();
        credentialDTO.setFailedLoginAttempts(0);

        credentialDTO.setPassword(encoder.encode(loginCredentials.getPassword()));
        Optional<CredentialDTO> credentialObj = Optional.of(credentialDTO);

        OAuthToken authenticationToken = new OAuthToken();
        Mockito.when(credentialsService.fetchCredentials(eq("domain"), eq("username"))).thenReturn(credentialObj);
        Mockito.when(jwtAndCookieAuthentication.authenticateUser(any(), any(), any(), any())).thenReturn(authenticationToken);

        OAuthToken oauthToken = authService.loginUser(http_request, http_response, loginCredentials);
        Assertions.assertEquals("No match for username/password combination", oauthToken.getProperties().get("error_msg"));
    }

    @Test
    void incorrectPassword() {
        PasswordEncoder encoder = new PasswordEncoder();
        LoginCredentials loginCredentials = generateLoginCredentials("domain",
                "username",
                "incorrectPassword");

        MockHttpServletRequest http_request = new MockHttpServletRequest();
        http_request.setRemoteAddr("127.0.0.1");
        MockHttpServletResponse http_response = new MockHttpServletResponse();

        CredentialDTO credentialDTO = new CredentialDTO();
        credentialDTO.setFailedLoginAttempts(0);

        credentialDTO.setPassword(encoder.encode("password"));
        Optional<CredentialDTO> credentialObj = Optional.of(credentialDTO);

        OAuthToken authenticationToken = new OAuthToken();
        Mockito.when(credentialsService.fetchCredentials(eq("domain"), eq("username"))).thenReturn(credentialObj);
        Mockito.when(jwtAndCookieAuthentication.authenticateUser(any(), any(), any(), any())).thenReturn(authenticationToken);

        OAuthToken oauthToken = authService.loginUser(http_request, http_response, loginCredentials);
        Assertions.assertEquals("No match for username/password combination", oauthToken.getProperties().get("error_msg"));
    }

    @Test
    void refreshAuthenticationToken() {
        MockHttpServletResponse http_response = new MockHttpServletResponse();

        // Mock JWT claims
        Map<String, Claim> claims = new HashMap<>();
        claims.put("dom", mockClaim("domain"));
        claims.put("data", mockClaim("username"));

        Mockito.when(jwtAndCookieAuthentication.verifyAuthentication(anyString(), anyString())).thenReturn(claims);

        // Mock lookup of claims embedded in JWT claims
        CredentialDTO credentialDTO = new CredentialDTO();
        credentialDTO.setUsername("username");
        credentialDTO.setDomain("domain");
        Optional<CredentialDTO> credentialObj = Optional.of(credentialDTO);

        Mockito.when(credentialsService.fetchCredentials(eq("domain"), eq("username"))).thenReturn(credentialObj);

        // Request generation of refresh token
        OAuthToken token = new OAuthToken();
        Mockito.when(jwtAndCookieAuthentication.refreshUserAuthentication(http_response, credentialDTO)).thenReturn(token);

        OAuthToken refreshToken = authService.refreshToken("authToken", "signature", http_response);
        Assertions.assertNotEquals(null, refreshToken);
    }

    @Test
    void refreshAuthenticationTokenWithInvalidUsername() {
        MockHttpServletResponse http_response = new MockHttpServletResponse();

        // Mock JWT claims
        Map<String, Claim> claims = new HashMap<>();
        claims.put("dom", mockClaim("domain"));
        claims.put("data", mockClaim("inValidUsername"));

        Mockito.when(jwtAndCookieAuthentication.verifyAuthentication(anyString(), anyString())).thenReturn(claims);

        // Mock lookup of claims embedded in JWT claims
        CredentialDTO credentialDTO = new CredentialDTO();
        credentialDTO.setUsername("username");
        credentialDTO.setDomain("domain");
        Optional<CredentialDTO> credentialObj = Optional.of(credentialDTO);

        Mockito.when(credentialsService.fetchCredentials(eq("domain"), eq("username"))).thenReturn(credentialObj);

        // Request generation of refresh token
        OAuthToken token = new OAuthToken();
        Mockito.when(jwtAndCookieAuthentication.refreshUserAuthentication(http_response, credentialDTO)).thenReturn(token);

        OAuthToken refreshToken = authService.refreshToken("authToken", "signature", http_response);
        Assertions.assertEquals("No match for username", refreshToken.getProperties().get("error_msg"));
    }

    @Test
    void refreshAuthenticationTokenWithNoClaims() {
        MockHttpServletResponse http_response = new MockHttpServletResponse();

        Mockito.when(jwtAndCookieAuthentication.verifyAuthentication(anyString(), anyString())).thenReturn(null);

        // Mock lookup of claims embedded in JWT claims
        CredentialDTO credentialDTO = new CredentialDTO();
        credentialDTO.setUsername("username");
        credentialDTO.setDomain("domain");
        Optional<CredentialDTO> credentialObj = Optional.of(credentialDTO);

        Mockito.when(credentialsService.fetchCredentials(eq("domain"), eq("username"))).thenReturn(credentialObj);

        // Request generation of refresh token
        OAuthToken token = new OAuthToken();
        Mockito.when(jwtAndCookieAuthentication.refreshUserAuthentication(http_response, credentialDTO)).thenReturn(token);

        OAuthToken refreshToken = authService.refreshToken("authToken", "signature", http_response);
        Assertions.assertEquals(null, refreshToken);
    }

    @Test
    void refreshAuthenticationTokenWithMissingClaims() {
        MockHttpServletResponse http_response = new MockHttpServletResponse();

        // Mock JWT claims
        Map<String, Claim> claims = new HashMap<>();

        Mockito.when(jwtAndCookieAuthentication.verifyAuthentication(anyString(), anyString())).thenReturn(claims);

        // Mock lookup of claims embedded in JWT claims
        CredentialDTO credentialDTO = new CredentialDTO();
        credentialDTO.setUsername("username");
        credentialDTO.setDomain("domain");
        Optional<CredentialDTO> credentialObj = Optional.of(credentialDTO);

        Mockito.when(credentialsService.fetchCredentials(eq("domain"), eq("username"))).thenReturn(credentialObj);

        // Request generation of refresh token
        OAuthToken token = new OAuthToken();
        Mockito.when(jwtAndCookieAuthentication.refreshUserAuthentication(http_response, credentialDTO)).thenReturn(token);

        OAuthToken refreshToken = authService.refreshToken("authToken", "signature", http_response);
        Assertions.assertEquals(null, refreshToken);
    }

    private LoginCredentials generateLoginCredentials(String domain, String username, String password) {
        LoginCredentials loginCredentials = new LoginCredentials();
        loginCredentials.setDomain(domain);
        loginCredentials.setUsername(username);
        loginCredentials.setPassword(password);
        return loginCredentials;
    }

    private Claim mockClaim(String value) {
        Claim mockedClaim = new Claim() {
            @Override
            public boolean isNull() {
                return false;
            }

            @Override
            public boolean isMissing() {
                return false;
            }

            @Override
            public Boolean asBoolean() {
                return null;
            }

            @Override
            public Integer asInt() {
                return null;
            }

            @Override
            public Long asLong() {
                return null;
            }

            @Override
            public Double asDouble() {
                return null;
            }

            @Override
            public String asString() {
                return value;
            }

            @Override
            public Date asDate() {
                return null;
            }

            @Override
            public <T> T[] asArray(Class<T> aClass) throws JWTDecodeException {
                return null;
            }

            @Override
            public <T> List<T> asList(Class<T> aClass) throws JWTDecodeException {
                return null;
            }

            @Override
            public Map<String, Object> asMap() throws JWTDecodeException {
                return null;
            }

            @Override
            public <T> T as(Class<T> aClass) throws JWTDecodeException {
                return null;
            }
        };

        return mockedClaim;
    }

    // refreshToken, logout(), authorizeClient()

}
