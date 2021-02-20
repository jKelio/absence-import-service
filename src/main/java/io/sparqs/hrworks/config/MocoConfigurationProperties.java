package io.sparqs.hrworks.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Validated
@Configuration
@ConfigurationProperties(prefix = "moco")
@Getter
@Setter
public class MocoConfigurationProperties {
    @NotNull(message = "base url shouldn't be null")
    @NotEmpty(message = "base url shouldn't be empty")
    private String baseUrl;

    @NotNull(message = "api key shouldn't be null")
    @NotEmpty(message = "api key shouldn't be empty")
    private String apiKey;
}
