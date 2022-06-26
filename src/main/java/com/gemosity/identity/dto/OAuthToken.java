package com.gemosity.identity.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class OAuthToken {
    String access_token;
    String token_type;
    Date expires_in;
    String scope;
    Map<String, String> properties;
}
