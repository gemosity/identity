package com.gemosity.user.service;

import com.gemosity.user.dto.LoginCredentials;
import com.gemosity.user.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
public class UsernameBasedAuthImpl implements IAuthService {
    @Override
    public UserDTO loginUser(LoginCredentials loginCredentials) {
        return null;
    }
}
