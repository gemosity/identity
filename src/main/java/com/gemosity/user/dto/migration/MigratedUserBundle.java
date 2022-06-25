package com.gemosity.user.dto.migration;

import com.gemosity.user.dto.CredentialDTO;
import com.gemosity.user.dto.SessionsDTO;
import com.gemosity.user.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MigratedUserBundle {
    private CredentialDTO credentials;
    private UserDTO userProfile;
    private SessionsDTO sessions;
}
