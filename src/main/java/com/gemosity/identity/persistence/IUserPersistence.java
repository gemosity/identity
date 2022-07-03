package com.gemosity.identity.persistence;

import com.gemosity.identity.dto.UserProfile;

import java.util.Map;

public interface IUserPersistence {
    UserProfile createUser(UserProfile userDTO);
    UserProfile updateUser(UserProfile userDTO);
    UserProfile deleteUser(UserProfile userDTO);

    UserProfile findByUuid(String userUuid);

    Map<String, Object> findMapByUuid(String userUuid);
}
