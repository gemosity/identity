package com.gemosity.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UserDTO {

    private String uuid;
    private String clientUuid;

    private Instant created;
    private Instant modified;
    private String firstName;
    private String lastName;

    private Instant createdDate;
    private Instant leavingDate;

    private String roles;

    private int maxConcurrentSessions;
}
