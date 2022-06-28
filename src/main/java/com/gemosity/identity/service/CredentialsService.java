package com.gemosity.identity.service;

import com.gemosity.identity.dto.CredentialDTO;
import com.gemosity.identity.dto.LoginCredentials;
import com.gemosity.identity.dto.UserDTO;
import com.gemosity.identity.persistence.couchbase.repository.CredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CredentialsService {

    private final CredentialRepository credentialsPersistence;

    @Autowired
    public CredentialsService(CredentialRepository credentialsPersistence) {
        this.credentialsPersistence = credentialsPersistence;
    }

    public CredentialDTO createCredentials(CredentialDTO user) {
        return credentialsPersistence.createCredentials(user);
    }

    public void updateCredentials(CredentialDTO userToUpdate) {
        credentialsPersistence.updateCredentials(userToUpdate);
    }

    public Optional<CredentialDTO> fetchCredentials(String domain, String username) {
        return credentialsPersistence.findByDomainAndUsername(domain, username);
    }
}
