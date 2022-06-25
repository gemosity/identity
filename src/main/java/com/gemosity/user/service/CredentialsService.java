package com.gemosity.user.service;

import com.gemosity.user.dto.CredentialDTO;
import com.gemosity.user.dto.LoginCredentials;
import com.gemosity.user.dto.UserDTO;
import com.gemosity.user.persistence.couchbase.repository.CredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CredentialsService {

    private final CredentialRepository credentialsPersistence;

    @Autowired
    public CredentialsService(UsernameBasedAuthImpl authService, CredentialRepository credentialsPersistence) {
        this.credentialsPersistence = credentialsPersistence;
    }

    public UserDTO loginUser(LoginCredentials loginCredentials) {
        return null;
    }

    public CredentialDTO createCredentials(CredentialDTO user) {
        return null;
    }

    public void updateCredentials(CredentialDTO userToDelete) {
    }

    public CredentialDTO fetchCredentials(String username) {
        return null;
    }
}
