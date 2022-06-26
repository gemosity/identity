package com.gemosity.identity.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuthServiceTests {
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void issueToken() {

    }

    @Test
    void verifyToken() {

    }
}
