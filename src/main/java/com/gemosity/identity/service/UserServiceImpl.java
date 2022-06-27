package com.gemosity.identity.service;

import com.gemosity.identity.util.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.gemosity.identity.dto.CredentialDTO;
import com.gemosity.identity.dto.OAuthToken;
import com.gemosity.identity.persistence.IUserPersistence;
import com.gemosity.identity.persistence.couchbase.repository.CredentialRepository;
import com.gemosity.identity.persistence.couchbase.repository.UserProfileRepository;
import com.gemosity.identity.dto.LoginCredentials;
import com.gemosity.identity.dto.UserDTO;
import com.gemosity.identity.util.AuthTokenWrapper;

@Service
public class UserServiceImpl implements IUserService {

    private final UsernameBasedAuthImpl authService;
    private final CredentialRepository credentialsRepository;
    private final IUserPersistence userPersistence;

    private static final int JWT_TOKEN_VALIDITY_IN_MINS = 17;

    @Autowired
    public UserServiceImpl(UsernameBasedAuthImpl authService,
                           CredentialRepository credentialsRepository,
                           UserProfileRepository userPersistence) {
        this.authService = authService;
        this.userPersistence = userPersistence;
        this.credentialsRepository = credentialsRepository;
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
        Optional<CredentialDTO> credentialObj = credentialsRepository.findByDomainAndUsername(loginCredentials.getDomain(), loginCredentials.getUsername());

        if (credentialObj.isPresent()) {

            CredentialDTO userCredentials = credentialObj.get();
            long failedLoginAttempts = userCredentials.getFailedLoginAttempts();

            if (failedLoginAttempts < 10) {

                boolean validPassword = passwordEncoder.verify(loginCredentials.getPassword(), userCredentials.getPassword());

                if (validPassword) {

                    authenticationToken = authService.authenticate(userCredentials, JWT_TOKEN_VALIDITY_IN_MINS);

                    if (authenticationToken == null) {
                        System.out.println("Error generating authenticationToken");
                    } else {

                        properties.put("last_successful_login", Long.toString(userCredentials.getLastSuccessfulLogin()));
                        properties.put("last_unsuccessful_login", Long.toString(userCredentials.getLastUnsuccessfulLogin()));

                        userCredentials.setLastSuccessfulLogin(instant.getEpochSecond());
                        userCredentials.setFailedLoginAttempts(0);

                        credentialsRepository.updateCredentials(userCredentials);
                    }
                } else {
                    failedLoginAttempts++;
                    userCredentials.setFailedLoginAttempts(failedLoginAttempts);
                    userCredentials.setLastUnsuccessfulLogin(instant.getEpochSecond());
                    properties.put("error_msg", "No match for username/password combination");
                    credentialsRepository.updateCredentials(userCredentials);
                }
            } else {
                properties.put("error_msg", "Account locked.");
                System.out.println("Account locked. Too many failed login attempts for " + loginCredentials.getUsername());
                userCredentials.setLastUnsuccessfulLogin(instant.getEpochSecond());
                credentialsRepository.updateCredentials(userCredentials);
            }
        } else {
            properties.put("error_msg", "No match for username/password combination");
            System.out.println("No match for username: " + loginCredentials.getUsername());
        }


        AuthenticationMethod authenticationMethod = new JwtAndCookieAuthentication();
        authenticationToken.setProperties(properties);
        authenticationMethod.authenticateUser(http_request, http_response, loginCredentials, authenticationToken);

        return ((JwtAndCookieAuthentication) authenticationMethod).getWrapper().getOauthToken();
    }

    private String getIpAddress(HttpServletRequest http_request) {
        return http_request.getRemoteAddr();
    }





    @Override
    public UserDTO createUser(UserDTO userDTO) {
        return userPersistence.createUser(userDTO);
    }

    @Override
    public UserDTO updateUser(UserDTO user) {
        return null;
    }

    @Override
    public UserDTO deleteUser(UserDTO user) {
        return null;
    }

    @Override
    public UserDTO fetchUser(String username) {

        //return userRepository.findByUsername(username);
        return null;
    }

    @Override
    public void logout(HttpServletResponse http_response) {

    }


}
