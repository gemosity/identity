package com.gemosity.user.persistence;

import com.gemosity.user.dto.CredentialDTO;

public interface ICredentialsPersistence {
    CredentialDTO createCredentials(CredentialDTO user);
}
