package com.gemosity.identity.service;

import com.gemosity.identity.dto.CredentialDTO;
import com.gemosity.identity.dto.OAuthToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {

    private final JwtService jwtService;

    public AuthService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public OAuthToken authenticate(CredentialDTO credentialDTO, int tokenValidityInMinutes) {
        System.out.println("AuthService authenticate");
        return jwtService.issueToken(credentialDTO, tokenValidityInMinutes);
    }
}
