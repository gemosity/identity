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

    private final AuthService authService;
    private final CredentialRepository credentialsRepository;
    private final IUserPersistence userPersistence;


    @Autowired
    public UserServiceImpl(AuthService authService,
                           CredentialRepository credentialsRepository,
                           UserProfileRepository userPersistence) {
        this.authService = authService;
        this.userPersistence = userPersistence;
        this.credentialsRepository = credentialsRepository;
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
