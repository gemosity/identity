package com.gemosity.identity.service;

import com.gemosity.identity.dto.LoginCredentials;
import com.gemosity.identity.dto.UserDTO;

public interface IAuthService {
    UserDTO loginUser(LoginCredentials loginCredentials);
}
