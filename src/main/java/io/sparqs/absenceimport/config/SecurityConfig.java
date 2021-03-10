package io.sparqs.absenceimport.config;

import io.sparqs.absenceimport.common.security.ApiKeyAuthenticationFilter;
import io.sparqs.absenceimport.common.security.ApiKeyAuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final HrWorksConfigurationProperties properties;

    SecurityConfig(HrWorksConfigurationProperties properties) {
        this.properties = properties;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setExposedHeaders(Collections.singletonList("authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        final ApiKeyAuthenticationFilter filter = new ApiKeyAuthenticationFilter();
        filter.setAuthenticationManager(new ApiKeyAuthenticationManager(properties));
        httpSecurity.antMatcher("/api/**")
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(STATELESS)
                .and().addFilter(filter).authorizeRequests().anyRequest().authenticated();
    }

}
