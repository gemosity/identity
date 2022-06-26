package com.gemosity.identity.service;

import com.gemosity.identity.dto.LoginCredentials;
import com.gemosity.identity.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
public class UsernameBasedAuthImpl implements IAuthService {
    @Override
    public UserDTO loginUser(LoginCredentials loginCredentials) {
        return null;
    }
}
