package com.gemosity.user.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidUtil {
    public String generateUuid() {
        return UUID.randomUUID().toString();
    }
}
