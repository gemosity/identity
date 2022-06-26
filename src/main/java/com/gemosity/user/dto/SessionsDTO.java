package com.gemosity.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class SessionsDTO {
    private String sessionUuid;
    private Instant created;
    private String userUuid;
    private Instant lastRequestedTime;
    private Instant maxInactiveInterval;
}
