package com.gemosity.identity.service;

import com.gemosity.identity.dto.UserProfile;

import java.util.Map;

public interface IUserService {

    UserProfile createUser(UserProfile userObj);

    UserProfile updateUser(UserProfile user);

    UserProfile deleteUser(UserProfile user);

    UserProfile findByUuid(String userUuid);
    Map<String, Object> findMapByUuid(String userUuid) ;

    String generateIDToken(String userUuid);

    Map<String, Object> fetchUserProfile(String authToken, String signature, String contentType);
}
