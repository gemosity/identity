package com.gemosity.identity.service;

import com.gemosity.identity.dto.CredentialDTO;
import com.gemosity.identity.persistence.couchbase.repository.CredentialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
class LoginServiceTests {

    private CredentialsService credentialsService;

    @Mock
    private CredentialRepository credentialsPersistence;

    @BeforeEach
    void setUp() {
        credentialsService = new CredentialsService(credentialsPersistence);
    }

    @Test
    void contextLoads() {
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
        Mockito.when(credentialsPersistence.findByDomainAndUsername(eq("domain"), eq(user.getUsername()))).thenReturn(Optional.of(user));
        Optional<CredentialDTO> returnedUser = credentialsService.fetchCredentials("domain", user.getUsername());
        assertEquals(returnedUser.get(), user);
    }

    @Test
    void fetchUserWithNullUsername() {
        CredentialDTO userDTO = new CredentialDTO();
        userDTO.setUsername(null);
        Optional<CredentialDTO> returnedUser = credentialsService.fetchCredentials("domain", userDTO.getUsername());
        assertEquals(returnedUser.isPresent(), false);
    }
}

