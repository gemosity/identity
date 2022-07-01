package com.gemosity.identity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gemosity.identity.persistence.IUserPersistence;
import com.gemosity.identity.persistence.couchbase.repository.CredentialRepository;
import com.gemosity.identity.persistence.couchbase.repository.UserProfileRepository;
import com.gemosity.identity.dto.UserProfile;

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
    public UserProfile createUser(UserProfile userDTO) {
        return userPersistence.createUser(userDTO);
    }

    @Override
    public UserProfile updateUser(UserProfile user) {
        return null;
    }

    @Override
    public UserProfile deleteUser(UserProfile user) {
        return null;
    }

    @Override
    public UserProfile findByUuid(String userUuid) {
        return userPersistence.findByUuid(userUuid);
    }

}
