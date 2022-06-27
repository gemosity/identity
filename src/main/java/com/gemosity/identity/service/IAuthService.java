package com.gemosity.identity.service;

import com.gemosity.identity.dto.CredentialDTO;
import com.gemosity.identity.dto.LoginCredentials;
import com.gemosity.identity.dto.OAuthToken;
import com.gemosity.identity.dto.UserDTO;

public interface IAuthService {
    OAuthToken authenticate(CredentialDTO userCredentials, int i);
}
