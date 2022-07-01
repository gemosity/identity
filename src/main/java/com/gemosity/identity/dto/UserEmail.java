package com.gemosity.identity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEmail {
    private String userUuid;

    private long created;
    private long email_verified;
    private long primaryEmailAddress;

    private String emailAddress;
    private long emailProviderId; // Assigned from pool
    private long emailDomainId;   // Assigned from pool

    private String emailAddressHash;
}
