package io.sparqs.hrworks.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "moco")
public class MocoConfigurationProperties {

    @Getter
    @Setter
    @NotNull(message = "base url shouldn't be null")
    @NotEmpty(message = "base url shouldn't be empty")
    private String baseUrl;

    @Getter
    @Setter
    @NotNull(message = "api key shouldn't be null")
    @NotEmpty(message = "api key shouldn't be empty")
    private String apiKey;

}
