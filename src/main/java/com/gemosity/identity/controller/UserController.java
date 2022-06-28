package com.gemosity.identity.controller;

import com.gemosity.identity.dto.LoginCredentials;
import com.gemosity.identity.dto.OAuthToken;
import com.gemosity.identity.dto.UserDTO;
import com.gemosity.identity.service.AuthenticationMethod;
import com.gemosity.identity.service.IUserService;
import com.gemosity.identity.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class UserController {

    private final IUserService userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/api/user/create")
    public UserDTO createUser(@RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    @PostMapping(path = "/api/user/update")
    public UserDTO updateUser(@RequestBody UserDTO userDTO) {
        return userService.updateUser(userDTO);
    }

    @PostMapping(path = "/api/user/delete")
    public UserDTO deleteUser(@RequestBody UserDTO userDTO) {
        return userService.deleteUser(userDTO);
    }

}
