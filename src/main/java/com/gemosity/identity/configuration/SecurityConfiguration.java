package com.gemosity.identity.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // Disable cors & csrf (needed for @PostMapping in controller?????)
        //http.cors().and().csrf().disable();

        // Disable CSRF (cross site request forgery)
        http.csrf().disable();

        // Instruct Spring Security to not create any user sessions
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        /*
              /migrate
         */

        // Specify credentials needed for different routes
        http.authorizeRequests()
                .antMatchers("/api/oauth/login").permitAll()
                .antMatchers("/api/oauth/logout").permitAll()
//                .antMatchers("/api/json/auth/addUser").permitAll()
//                .antMatchers("/api/json/auth/changePassword").permitAll()
                .anyRequest().authenticated();

        // Configure Access Denied Page for user without sufficient permissions
        http.exceptionHandling().accessDeniedPage("/login");

        http.addFilter(new TokenFilter(authenticationManager()));
    }
}
