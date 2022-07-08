package com.gemosity.identity.controller;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.gemosity.identity.dto.UserProfile;
import com.gemosity.identity.service.IUserService;
import com.gemosity.identity.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private final IUserService userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/api/user/create")
    public UserProfile createUser(@RequestBody UserProfile userDTO) {
        return userService.createUser(userDTO);
    }

    @PostMapping(path = "/api/user/update")
    public UserProfile updateUser(@RequestBody UserProfile userDTO) {
        return userService.updateUser(userDTO);
    }

    @PostMapping(path = "/api/user/delete")
    public UserProfile deleteUser(@RequestBody UserProfile userDTO) {
        return userService.deleteUser(userDTO);
    }


    @GetMapping(value = "/oidc/userinfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserProfile userInfoEndpoint(@CookieValue(value = "sessionId") String signature,
                                                @RequestHeader(value="Authorization") String authToken) {

        return userService.fetchUserProfile(authToken, signature);
    }

    @JsonAnyGetter
    @GetMapping(value = "/oidc/userinfo/jwt")
    public Map<String, Object> userInfoEndpointAsJwt(HttpServletResponse http_response, @CookieValue(value = "sessionId") String signature,
                                                     @RequestHeader(value="Authorization") String authToken) {

        return userService.fetchUserProfileAsMap(authToken, signature);
    }

//    @JsonAnyGetter
//    @GetMapping(path = "/oidc/userinfo/jwt")
//    public Map<String, Object> userInfoEndpointTest(@CookieValue(value = "sessionId") String signature,
//                                                @RequestHeader(value="Authorization") String authToken) {
//
//        /*
//               application/json
//               application/jwt (OAuthToken with id_token set???)
//                Requires valid access token to lookup the user info. The endpoint returns all the associated claims
//                granted. Requires at least openid scope.
//                HTTP/1.1 200 OK
//                Content-Type: application/json
//                {
//                    "given_name": "Joe",
//                    "family_name": "Bloggs",
//                    "name": "Joe Bloggs",
//                    "sub": "4353456346",
//                    "role": [
//                        "admin",
//                    ]
//                }
//         */
//
//        List<String> roles = new ArrayList<>();
//        roles.add("admin");
//        roles.add("user");
//
//        Map<String, Object> props = new HashMap<>();
//        props.put("given_name", "Joe");
//        props.put("family_name", "Bloggs");
//        props.put("name", "Joe Bloggs");
//        props.put("role", roles);
//
//        return props;
//    }
}
