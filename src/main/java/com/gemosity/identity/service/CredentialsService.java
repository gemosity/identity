package com.gemosity.identity.service;

import com.gemosity.identity.dto.CredentialDTO;
import com.gemosity.identity.dto.LoginCredentials;
import com.gemosity.identity.dto.UserDTO;
import com.gemosity.identity.persistence.couchbase.repository.CredentialRepository;
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
