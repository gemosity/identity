package com.gemosity.identity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private String uuid;
    private String clientUuid;

    private long created;
    private long modified;
    private String firstName;
    private String lastName;

    private long createdDate;
    private long leavingDate;

    private String roles;

    private int maxConcurrentSessions;
}
