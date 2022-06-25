package com.gemosity.user.controller;

import com.gemosity.user.dto.UserDTO;
import com.gemosity.user.persistence.couchbase.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {
    private final UserProfileRepository couchbaseService;

    @Autowired
    public TestController(UserProfileRepository couchbaseService) {
        this.couchbaseService = couchbaseService;
    }

    @GetMapping("/cb")
    public UserDTO test() {
       return couchbaseService.test();
    }
}
