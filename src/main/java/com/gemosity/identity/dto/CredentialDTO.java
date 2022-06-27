package com.gemosity.identity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CredentialDTO {
    private String uuid;
    private String clientUuid;

    private String domain;
    private String username;

    private String password;

    private String passwordAlgorithm;

    private boolean active;
    private String resetEmailAddress;

    private long created;
    private long modified;

    private long lastSuccessfulLogin;
    private long lastUnsuccessfulLogin;
    private long failedLoginAttempts;


    // OTP
    // - don't prompt for OTP for a week after successful login (checkbox)
}
