package com.gemosity.identity.persistence;

import com.gemosity.identity.dto.CredentialDTO;

import java.util.Optional;

public interface ICredentialsPersistence {
    CredentialDTO createCredentials(CredentialDTO credentialDTO);
    CredentialDTO updateCredentials(CredentialDTO credentialDTO);

    Optional<CredentialDTO> findByDomainAndUsername(String domain, String username);
}
