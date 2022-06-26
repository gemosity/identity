package com.gemosity.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionsDTO {
    private String sessionUuid;
    private long created;
    private String userUuid;
    private long lastRequestedTime;
    private long maxInactiveInterval;
}
