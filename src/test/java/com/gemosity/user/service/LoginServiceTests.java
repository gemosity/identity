package com.gemosity.user.service;

import com.gemosity.user.dto.CredentialDTO;
import com.gemosity.user.persistence.couchbase.repository.CredentialRepository;
import com.gemosity.user.dto.LoginCredentials;
import com.gemosity.user.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoginServiceTests {

    private CredentialsService credentialsService;

    @Mock
    private CredentialRepository credentialsPersistence;

    @Mock
    private UsernameBasedAuthImpl authService;

    @BeforeEach
    void setUp() {
        credentialsService = new CredentialsService(authService, credentialsPersistence);
    }

    @Test
    void contextLoads() {
    }

    @Test
    void loginUser() {
        // User micro-service should handle login user to support multiple auth methods i.e. email, ldap, oauth etc.
        // mock service that communicates with Auth micro-service
        LoginCredentials loginCredentials = new LoginCredentials();

        Mockito.when(authService.loginUser(loginCredentials)).thenReturn(new UserDTO());

        UserDTO userDTO = credentialsService.loginUser(loginCredentials);
    }

    @Test
    void loginWithTenFailedAttempts() {
        // User micro-service should handle login user to support multiple auth methods i.e. email, ldap, oauth etc.
    }

    @Test
    void createUserCredentials() {
        CredentialDTO user = new CredentialDTO();
        CredentialDTO success = credentialsService.createCredentials(user);
        Mockito.verify(credentialsPersistence).createCredentials(user);
    }

    @Test
    void createUserWithNullUsername() {
        CredentialDTO user = new CredentialDTO();
        user.setUsername(null);
        CredentialDTO success = credentialsService.createCredentials(user);
        assertNull(success);
    }

    @Test
    void createUserWithNonAlphaNumericUsername() {
        CredentialDTO userWithNonAlphaNumericUsername = new CredentialDTO();
        userWithNonAlphaNumericUsername.setUsername("select * from user;");
        CredentialDTO success = credentialsService.createCredentials(userWithNonAlphaNumericUsername);
        assertNull(success);
    }

    @Test
    void updateUser() {
        CredentialDTO userToUpdate = new CredentialDTO();
        userToUpdate.setUsername("updatedUsername");
        credentialsService.updateCredentials(userToUpdate);
    }

    @Test
    void deleteUser() {
        CredentialDTO userToDelete = new CredentialDTO();
        credentialsService.updateCredentials(userToDelete);
        //  Mockito.verify(userService).delete(userToDelete);
    }

    @Test
    void deactivateUser() {
        CredentialDTO userToUpdate = new CredentialDTO();
        userToUpdate.setActive(false);
        credentialsService.updateCredentials(userToUpdate);
    }

    @Test
    void activateUser() {
        CredentialDTO userToUpdate = new CredentialDTO();
        userToUpdate.setActive(true);
        credentialsService.updateCredentials(userToUpdate);
    }

    @Test
    void fetchUserByUsername() {
        CredentialDTO user = new CredentialDTO();
        user.setUsername("user1");
        Mockito.when(credentialsService.fetchCredentials(user.getUsername())).thenReturn(user);
        CredentialDTO returnedUser = credentialsService.fetchCredentials(user.getUsername());
        Mockito.verify(credentialsService).fetchCredentials(user.getUsername());
        assertEquals(returnedUser, user);
    }

    @Test
    void fetchUserWithNullUsername() {
        CredentialDTO userDTO = new CredentialDTO();
        userDTO.setUsername(null);
        CredentialDTO user = credentialsService.fetchCredentials(userDTO.getUsername());
        assertEquals(user, null);
    }
}

