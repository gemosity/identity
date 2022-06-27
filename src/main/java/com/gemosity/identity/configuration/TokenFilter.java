package com.gemosity.identity.configuration;

import com.auth0.jwt.interfaces.Claim;
import com.gemosity.identity.service.JwtService;
import com.gemosity.identity.util.SecretLoader;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TokenFilter extends BasicAuthenticationFilter {

    private JwtService authService;

    public TokenFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        SecretLoader secretLoader = new SecretLoader();
        authService = new JwtService(secretLoader);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {


        String http_header = request.getHeader("Authorization");

        // If no Bearer Authorization header, then pass request along filter chain.
        if (http_header == null || !http_header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = verifyAuthentication(request);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken verifyAuthentication(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String signature = "";

        if(cookies == null) {
            return null;
        }

        String json_auth_str = request.getHeader("Authorization");

        json_auth_str = json_auth_str.replace("Bearer ", "");

        for(Cookie cookie: cookies) {
            if(cookie.getName().equalsIgnoreCase("sessionId")) {
                signature = cookie.getValue();
            }
        }

        // verify JWT token
        if (json_auth_str != null && !json_auth_str.isEmpty()) {

            Map<String, Claim> claims = authService.verifyToken(json_auth_str, signature);

            if (claims != null) {
                // Principle is typically the username
                String principal = claims.get("data").asString();

                // Credentials stores the password or something to prove the principle is valid.
                // Use JWT in our case.
                List<String> credentials = new ArrayList<>();
                credentials.add(json_auth_str);
                credentials.add(signature);

                List<GrantedAuthority> authorityList = new ArrayList<>();
                authorityList.add(new GrantedAuthorityImpl("ROLE_ADMIN"));

                UsernamePasswordAuthenticationToken authenticationToken
                        = new UsernamePasswordAuthenticationToken(principal, credentials, authorityList);

                // Details can contain IP address, serial number etc
                // authenticationToken.setDetails();
                return authenticationToken;
            }
            return null;
        }

        return null;
    }
}

