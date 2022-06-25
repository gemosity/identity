package com.gemosity.user.service;

import com.gemosity.user.dto.LoginCredentials;
import com.gemosity.user.dto.UserDTO;

public interface IAuthService {
    UserDTO loginUser(LoginCredentials loginCredentials);
}
