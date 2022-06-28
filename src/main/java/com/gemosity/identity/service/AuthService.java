package com.gemosity.identity.service;

import com.auth0.jwt.interfaces.Claim;
import com.gemosity.identity.dto.CredentialDTO;
import com.gemosity.identity.dto.LoginCredentials;
import com.gemosity.identity.dto.OAuthToken;
import com.gemosity.identity.util.AuthTokenWrapper;
import com.gemosity.identity.util.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService implements IAuthService {

    private static final int JWT_TOKEN_VALIDITY_IN_MINS = 17;

    private final AuthenticationMethod authenticationMethod;
    private final CredentialsService credentialsService;

    public AuthService(CredentialsService credentialsService, JwtAndCookieAuthentication jwtService) {
        this.credentialsService = credentialsService;
        this.authenticationMethod = jwtService;
    }

    public OAuthToken loginUser(HttpServletRequest http_request,
                                HttpServletResponse http_response,
                                LoginCredentials loginCredentials) {

        AuthTokenWrapper wrapper = new AuthTokenWrapper();
        PasswordEncoder passwordEncoder = new PasswordEncoder();

        OAuthToken authenticationToken = new OAuthToken();
        Map<String, String> properties = new HashMap<String, String>();

        System.out.println(getIpAddress(http_request));

        Instant instant = Instant.now();

        System.out.println("domain:" + loginCredentials.getDomain() + " user " + loginCredentials.getUsername());
        Optional<CredentialDTO> credentialObj = credentialsService.fetchCredentials(loginCredentials.getDomain(), loginCredentials.getUsername());

        if (credentialObj.isPresent()) {

            CredentialDTO userCredentials = credentialObj.get();
            System.out.println("Unpack creds: " + userCredentials.getUsername() + " clientUuid: " + userCredentials.getClientUuid());
            long failedLoginAttempts = userCredentials.getFailedLoginAttempts();

            if (failedLoginAttempts < 10) {

                boolean validPassword = passwordEncoder.verify(loginCredentials.getPassword(), userCredentials.getPassword());

                if (validPassword) {
                    System.out.println("Valid password");

                    authenticationToken = authenticationMethod.authenticateUser(http_request, http_response, userCredentials);

                    if (authenticationToken == null) {
                        System.out.println("Error generating authenticationToken");
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
                System.out.println("Account locked. Too many failed login attempts for " + loginCredentials.getUsername());
                userCredentials.setLastUnsuccessfulLogin(instant.getEpochSecond());
                credentialsService.updateCredentials(userCredentials);
            }
        } else {
            properties.put("error_msg", "No match for username/password combination");
            System.out.println("No match for username: " + loginCredentials.getUsername());
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

            oauthToken = new OAuthToken();
            String domain = claims.get("dom").asString();
            String data = claims.get("data").asString();
            Optional<CredentialDTO> credentialObj = credentialsService.fetchCredentials(domain, data);

            if (credentialObj.isPresent()) {
                CredentialDTO specifiedUser = credentialObj.get();

                oauthToken = authenticationMethod.refreshUserAuthentication(http_response, specifiedUser);

            } else {
                properties.put("error_msg", "No match for username");
                System.out.println("No match for username ");
            }

            oauthToken.setProperties(properties);
        }

        return oauthToken;
    }
}
