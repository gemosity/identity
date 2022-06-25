package com.gemosity.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class SessionsDTO {
    private Instant lastSuccessfulLogin;
    private Instant lastUnsuccessfulLogin;
    private long failedLoginAttempts;

}
