package com.gemosity.identity.util;

import com.gemosity.identity.dto.OAuthToken;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.Cookie;
import java.util.List;

@Getter
@Setter
public class AuthTokenWrapper {
    OAuthToken oauthToken;
    List<Cookie> cookieList;
}
