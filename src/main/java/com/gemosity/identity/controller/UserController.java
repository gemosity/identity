package com.gemosity.identity.controller;

import com.gemosity.identity.dto.UserProfile;
import com.gemosity.identity.service.IUserService;
import com.gemosity.identity.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

}
