package com.gemosity.identity.util;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

public class PasswordEncoder {
    Argon2PasswordEncoder encoder;

    public PasswordEncoder() {
        encoder = new Argon2PasswordEncoder(32, 32, 1, 65536, 14);
    }

    public String encode(String cleartext) {

        String hash = encoder.encode(cleartext);
        return hash;
    }

    public boolean verify(String cleartext, String hash) {
        return encoder.matches(cleartext, hash);
    }

}
