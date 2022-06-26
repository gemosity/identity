package com.gemosity.identity.service;

import com.gemosity.identity.dto.LoginCredentials;
import com.gemosity.identity.dto.UserDTO;

public interface IUserService {
    UserDTO loginUser(LoginCredentials loginCredentials);

    UserDTO createUser(UserDTO userObj);

    UserDTO updateUser(UserDTO user);

    UserDTO deleteUser(UserDTO user);

    UserDTO fetchUser(String username);

}
