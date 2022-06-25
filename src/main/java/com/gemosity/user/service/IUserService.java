package com.gemosity.user.service;

import com.gemosity.user.dto.LoginCredentials;
import com.gemosity.user.dto.UserDTO;

public interface IUserService {
    UserDTO loginUser(LoginCredentials loginCredentials);

    UserDTO createUser(UserDTO userObj);

    UserDTO updateUser(UserDTO user);

    UserDTO deleteUser(UserDTO user);

    UserDTO fetchUser(String username);

}
