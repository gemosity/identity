package com.gemosity.identity.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserProfile {

    private String uuid;
    private String clientUuid;

    // Following names require "profile" scope
    private String given_name;
    private String middle_name;
    private String family_name;
    private String nickname;

    private long created_at;
    private long updated_at;
    private long leaving_date;

    private String picture;
    private List<String> roles;

    private int maxConcurrentSessions;
}
