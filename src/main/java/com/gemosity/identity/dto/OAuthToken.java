package com.gemosity.identity.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class OAuthToken {
    private String access_token;
    private String refresh_token;
    private String id_token;
    private String token_type;
    private Date expires_in;
    private String scope;
    private Map<String, String> properties;
}
