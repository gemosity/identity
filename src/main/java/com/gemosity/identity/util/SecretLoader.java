package com.gemosity.identity.util;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class SecretLoader {
    // byte[] falls outside scope of GC. Hence more secure.
    public byte[] loadSecret() {

        try {
            try {
                return Files.readAllBytes(Paths.get("/home/identity/secret.txt"));
            } catch (java.nio.file.NoSuchFileException e) {
                return Files.readAllBytes(Paths.get("/Users/gemosity/secret.txt"));
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }

        return null;
    }
}
