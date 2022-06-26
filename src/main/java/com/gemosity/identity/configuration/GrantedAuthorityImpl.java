package com.gemosity.identity.configuration;

import org.springframework.security.core.GrantedAuthority;

public class GrantedAuthorityImpl implements GrantedAuthority {

    private String grantedRole;

    public GrantedAuthorityImpl(String grantedRole) {
        this.grantedRole = grantedRole;
    }

    @Override
    public String getAuthority() {
        return grantedRole;
    }
}
