package com.gemosity.identity.service;

import com.gemosity.identity.dto.UserProfile;

public interface IUserService {

    UserProfile createUser(UserProfile userObj);

    UserProfile updateUser(UserProfile user);

    UserProfile deleteUser(UserProfile user);

    UserProfile findByUuid(String userUuid);
}
