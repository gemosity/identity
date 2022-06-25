package com.gemosity.user.persistence.couchbase;

import com.gemosity.user.dto.LoginCredentials;
import com.gemosity.user.dto.UserDTO;
import com.gemosity.user.persistence.couchbase.repository.UserProfileRepository;
import com.gemosity.user.service.UserServiceImpl;
import com.gemosity.user.service.UsernameBasedAuthImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class UserProfileRepositoryTests {

    private UserServiceImpl userService;

    @Mock
    private UserProfileRepository userPersistence;

    @Mock
    private UsernameBasedAuthImpl authService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(authService, userPersistence);
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

        UserDTO userDTO = userService.loginUser(loginCredentials);
    }

    @Test
    void loginWithTenFailedAttempts() {
        // User micro-service should handle login user to support multiple auth methods i.e. email, ldap, oauth etc.
    }

    @Test
    void createUser() {
        UserDTO user = new UserDTO();
        UserDTO success = userService.createUser(user);
        Mockito.verify(userPersistence).createUser(user);
    }

    @Test
    void createUserWithNullUsername() {
        UserDTO user = new UserDTO();
        user.setFirstName("Joe");
        user.setLastName("Bloggs");
        UserDTO success = userService.createUser(user);
        assertNull(success);
    }

    @Test
    void createUserWithNonAlphaNumericUsername() {
        UserDTO userWithNonAlphaNumericUsername = new UserDTO();
        userWithNonAlphaNumericUsername.setFirstName("select * from user;");
        userWithNonAlphaNumericUsername.setLastName("Bloggs");
        UserDTO success = userService.createUser(userWithNonAlphaNumericUsername);
        assertNull(success);
    }

    @Test
    void updateUser() {
        UserDTO userToUpdate = new UserDTO();
        userToUpdate.setFirstName("Joe");
        userToUpdate.setLastName("Bloggs Blogging");
        userService.updateUser(userToUpdate);
    }

    @Test
    void deleteUser() {
        UserDTO userToDelete = new UserDTO();
        userService.deleteUser(userToDelete);
        //  Mockito.verify(userService).delete(userToDelete);
    }
}
