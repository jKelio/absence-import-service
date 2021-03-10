package io.sparqs.absenceimport.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "mail")
public class MailConfigurationProperties {

    @Getter
    @Setter
    @NotNull(message = "mail api key shouldn't be null")
    @NotEmpty(message = "mail api key shouldn't be empty")
    private String apiKey;

}
