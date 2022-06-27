package com.gemosity.identity.service;

import com.gemosity.identity.dto.LoginCredentials;
import com.gemosity.identity.dto.OAuthToken;
import com.gemosity.identity.dto.UserDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IUserService {
    OAuthToken loginUser(HttpServletRequest http_request,
                         HttpServletResponse http_response,
                         LoginCredentials loginCredentials);

    UserDTO createUser(UserDTO userObj);

    UserDTO updateUser(UserDTO user);

    UserDTO deleteUser(UserDTO user);

    UserDTO fetchUser(String username);

    void logout(HttpServletResponse http_response);
}
