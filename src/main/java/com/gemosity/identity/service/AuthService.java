package com.gemosity.identity.service;

import com.auth0.jwt.interfaces.Claim;
import com.gemosity.identity.dto.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.gemosity.identity.util.AuthTokenWrapper;
import com.gemosity.identity.util.PasswordEncoder;

@Service
public class AuthService implements IAuthService {

    private static final Logger log = LogManager.getLogger(AuthService.class);

    private final AuthenticationMethod authenticationMethod;
    private final IUserService userService;
    private final CredentialsService credentialsService;
    private final JwtService jwtService;

    public AuthService(CredentialsService credentialsService, UserServiceImpl userService,
                       JwtAndCookieAuthentication jwtAndCookieAuthentication,
                       JwtService jwtService) {
        this.credentialsService = credentialsService;
        this.userService = userService;
        this.authenticationMethod = jwtAndCookieAuthentication;
        this.jwtService = jwtService;
    }

    public OAuthToken loginUser(HttpServletRequest http_request,
                                HttpServletResponse http_response,
                                LoginCredentials loginCredentials) {

        AuthTokenWrapper wrapper = new AuthTokenWrapper();
        PasswordEncoder passwordEncoder = new PasswordEncoder();

        OAuthToken authenticationToken = new OAuthToken();
        Map<String, String> properties = new HashMap<String, String>();

        log.info("Login user from " + getIpAddress(http_request));

        Instant instant = Instant.now();

        log.info("domain:" + loginCredentials.getDomain() + " user " + loginCredentials.getUsername());
        Optional<CredentialDTO> credentialObj = credentialsService.fetchCredentials(loginCredentials.getDomain(),
                loginCredentials.getUsername());

        if (credentialObj.isPresent()) {

            CredentialDTO userCredentials = credentialObj.get();
            long failedLoginAttempts = userCredentials.getFailedLoginAttempts();

            if (failedLoginAttempts < 10) {

                boolean validPassword = passwordEncoder.verify(loginCredentials.getPassword(), userCredentials.getPassword());

                if (validPassword) {
                    log.info("Password is valid");

                    //UserProfile userProfile = userService.findByUuid(userCredentials.getUuid());
                    Map<String, Object> userProfile = userService.findMapByUuid(userCredentials.getUuid());

                    String idToken = jwtService.generateIDToken(userProfile, "profile");
                    System.out.println(idToken);

                    authenticationToken = authenticationMethod.authenticateUser(http_request,
                            http_response,
                            userCredentials,
                            idToken);

                    if (authenticationToken == null) {
                        log.error("Unable to generate authenticationToken");
                    } else {

                        properties.put("last_successful_login", Long.toString(userCredentials.getLastSuccessfulLogin()));
                        properties.put("last_unsuccessful_login", Long.toString(userCredentials.getLastUnsuccessfulLogin()));

                        userCredentials.setLastSuccessfulLogin(instant.getEpochSecond());
                        userCredentials.setFailedLoginAttempts(0);

                        credentialsService.updateCredentials(userCredentials);
                    }
                } else {
                    failedLoginAttempts++;
                    userCredentials.setFailedLoginAttempts(failedLoginAttempts);
                    userCredentials.setLastUnsuccessfulLogin(instant.getEpochSecond());
                    properties.put("error_msg", "No match for username/password combination");
                    credentialsService.updateCredentials(userCredentials);
                }
            } else {
                properties.put("error_msg", "Account locked.");
                log.info("Account locked. Too many failed login attempts for " + loginCredentials.getUsername());
                userCredentials.setLastUnsuccessfulLogin(instant.getEpochSecond());
                credentialsService.updateCredentials(userCredentials);
            }
        } else {
            properties.put("error_msg", "No match for username/password combination");
            log.info("No match for username: " + loginCredentials.getUsername());
        }

        authenticationToken.setProperties(properties);

        return authenticationToken;
    }

    private String getIpAddress(HttpServletRequest http_request) {
        return http_request.getRemoteAddr();
    }

    @Override
    public void logout(HttpServletResponse http_response) {
        authenticationMethod.logout(http_response);
    }

    @Override
    public OAuthToken refreshToken(String authToken, String signature, HttpServletResponse http_response) {
        AuthTokenWrapper wrapper = new AuthTokenWrapper();
        OAuthToken oauthToken = null;
        Map<String, String> properties = new HashMap<String, String>();

        // Decode the JWT to help identify the logged in user
        String json_auth_str = authToken.replace("Bearer ", "");
        Map<String, Claim>  claims = authenticationMethod.verifyAuthentication(json_auth_str, signature);

        if(claims != null) {

            Claim domainClaim = claims.get("dom");
            Claim dataClaim = claims.get("data");

            if(domainClaim != null && dataClaim != null) {
                oauthToken = new OAuthToken();

                Optional<CredentialDTO> credentialObj = credentialsService.fetchCredentials(domainClaim.asString(),
                        dataClaim.asString());

                if (credentialObj.isPresent()) {
                    CredentialDTO specifiedUser = credentialObj.get();

                    oauthToken = authenticationMethod.refreshUserAuthentication(http_response, specifiedUser);

                } else {
                    properties.put("error_msg", "No match for username");
                    log.info("No match for username ");
                }

                oauthToken.setProperties(properties);
            }
        }

        return oauthToken;
    }

    @Override
    public OAuthToken requestToken(TokenRequest tokenRequest, String authToken, String signature,
                                   HttpServletResponse http_response) {
        // Client may request different scope
        // tokenRequest.getScope()

        OAuthToken oAuthToken = null;
        if(tokenRequest.getGrant_type() == "refresh_token") {
            oAuthToken = refreshToken(authToken, signature, http_response);
        }

        // 'authorization_code' - Convert token returned from /authorize into an Access Token.
        // 'refresh_token' - Passed in a refresh_token returns a new Access Token and Refresh Token.
        // 'client_credentials' - Returns an Access Token. No refresh token is included.

        return oAuthToken;
    }

    public OAuthToken authorizeClient(OAuthAuthorizeRequest oAuthAuthorizeRequest,
                                      HttpServletResponse http_response) {
        String responseType = oAuthAuthorizeRequest.getResponse_type();

        // Login user, check user wants to authorize client

        // HTTP 302 FOUND - Redirect to application URI
        http_response.setStatus(HttpServletResponse.SC_FOUND);
        http_response.setHeader("Location",
                oAuthAuthorizeRequest.getRedirect_uri() + "/callback?code" + "AUTHORIZATION_CODE");

        return new OAuthToken();
    }

}
