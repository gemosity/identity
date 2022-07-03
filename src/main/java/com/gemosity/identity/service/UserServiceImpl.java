package com.gemosity.identity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gemosity.identity.persistence.IUserPersistence;
import com.gemosity.identity.persistence.couchbase.repository.CredentialRepository;
import com.gemosity.identity.persistence.couchbase.repository.UserProfileRepository;
import com.gemosity.identity.dto.UserProfile;

import java.util.Map;

@Service
public class UserServiceImpl implements IUserService {

    private final CredentialRepository credentialsRepository;
    private final IUserPersistence userPersistence;
    private final JwtService jwtService;

    @Autowired
    public UserServiceImpl(
                           CredentialRepository credentialsRepository,
                           UserProfileRepository userPersistence,
                           JwtService jwtService) {
        this.userPersistence = userPersistence;
        this.credentialsRepository = credentialsRepository;
        this.jwtService = jwtService;
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

    @Override
    public String generateIDToken(String userUuid) {
        UserProfile userProfile = userPersistence.findByUuid(userUuid);

        return null;
    }

    @Override
    public Map<String, Object> fetchUserProfile(String authToken, String signature, String contentType) {
        UserProfile userProfile = userPersistence.findByUuid("");

        if(contentType.contentEquals("application/jwt")) {
            String idTokenJWT = jwtService.generateIDToken(userProfile);
        }
        return null;
    }

}
