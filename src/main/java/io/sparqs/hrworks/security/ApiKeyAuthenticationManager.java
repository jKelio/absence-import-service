package io.sparqs.hrworks.security;

import io.sparqs.hrworks.config.HrWorksConfigurationProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyAuthenticationManager implements AuthenticationManager {

    private final HrWorksConfigurationProperties properties;

    ApiKeyAuthenticationManager(HrWorksConfigurationProperties properties) {
        this.properties = properties;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String principal = (String) authentication.getPrincipal();
        if (!properties.getPublicAccessKey().equals(principal))
        {
            throw new BadCredentialsException("Public Access Key is invalid");
        }
        authentication.setAuthenticated(true);
        return authentication;
    }
}
