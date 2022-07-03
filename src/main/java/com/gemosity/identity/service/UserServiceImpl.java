package com.gemosity.identity.service;

import com.auth0.jwt.interfaces.Claim;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gemosity.identity.persistence.IUserPersistence;
import com.gemosity.identity.persistence.couchbase.repository.CredentialRepository;
import com.gemosity.identity.persistence.couchbase.repository.UserProfileRepository;
import com.gemosity.identity.dto.UserProfile;

import java.util.HashMap;
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
        Map<String, Object> retValue = null;
        Map<String, Claim> claims = jwtService.verifyToken(authToken, signature);

        String subUuid = claims.get("sub").asString();

        UserProfile userProfile = userPersistence.findByUuid(subUuid);
        String idTokenJWT = null;
        if(contentType.contentEquals("application/jwt")) {
             idTokenJWT = jwtService.generateIDToken(userProfile);
             retValue = new HashMap<>();
             retValue.put("id", idTokenJWT);
        }

        return retValue;
    }

}
