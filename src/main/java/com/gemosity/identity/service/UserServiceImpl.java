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
    public Map<String, Object> findMapByUuid(String userUuid) {
        Map<String, Object> userProfileMap = userPersistence.findMapByUuid(userUuid);
        String fullName;

        if(userProfileMap.get("name") == null) {
            if(userProfileMap.get("given_name") != null) {
                if (userProfileMap.get("family_name") != null) {
                    fullName = (String) userProfileMap.get("given_name");
                    fullName = fullName.trim();
                    fullName = fullName + " " + userProfileMap.get("family_name");
                } else {
                    fullName = (String) userProfileMap.get("given_name");
                    fullName = fullName.trim();
                }

                userProfileMap.put("name", fullName);
            }
        }

        return userProfileMap;
    }

    @Override
    public UserProfile findByUuid(String userUuid) {
        return userPersistence.findByUuid(userUuid);
    }

    @Override
    public Map<String, Object> fetchUserProfile(String authToken, String signature, String contentType) {
        Map<String, Object> retValue = null;
        Map<String, Claim> claims = jwtService.verifyToken(authToken, signature);

        String subUuid = claims.get("sub").asString();
        Map<String, Object> userProfile = userPersistence.findMapByUuid(subUuid);
        String idTokenJWT = null;

        if(contentType.contentEquals("application/jwt")) {
             idTokenJWT = jwtService.generateIDToken(userProfile, "profile");
             retValue = new HashMap<>();
             retValue.put("id", idTokenJWT);
        }

        return retValue;
    }

}
