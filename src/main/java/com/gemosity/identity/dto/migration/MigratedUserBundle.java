package com.gemosity.identity.dto.migration;

import com.gemosity.identity.dto.CredentialDTO;
import com.gemosity.identity.dto.SessionsDTO;
import com.gemosity.identity.dto.UserProfile;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MigratedUserBundle {
    private CredentialDTO credentials;
    private UserProfile userProfile;
    private SessionsDTO sessions;
}
