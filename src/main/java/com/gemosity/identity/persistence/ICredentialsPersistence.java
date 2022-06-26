package com.gemosity.identity.persistence;

import com.gemosity.identity.dto.CredentialDTO;

public interface ICredentialsPersistence {
    CredentialDTO createCredentials(CredentialDTO user);
}
