package io.sparqs.hrworks.security;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class ApiKeyAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    public static final String AUTHORIZATION = "Authorization";

    ApiKeyAuthenticationFilter(ApiKeyAuthenticationManager manager) {
        setAuthenticationManager(manager);
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }

}
