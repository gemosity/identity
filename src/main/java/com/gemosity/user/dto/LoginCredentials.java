package com.gemosity.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginCredentials {
    // Login credentials supplied from web browser to login
    private String domain;
    private String username;
    private String password;
    private String loginMethod;  // Username, LDAP etc

    // rememberMe ??
}
