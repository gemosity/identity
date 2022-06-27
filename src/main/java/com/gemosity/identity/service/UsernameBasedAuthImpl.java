package com.gemosity.identity.service;

import com.gemosity.identity.dto.CredentialDTO;
import com.gemosity.identity.dto.LoginCredentials;
import com.gemosity.identity.dto.OAuthToken;
import com.gemosity.identity.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
public class UsernameBasedAuthImpl implements IAuthService {

    @Override
    public OAuthToken authenticate(CredentialDTO userCredentials, int i) {
        return null;
    }
}
