package io.sparqs.hrworks.config;

import com.aoe.hrworks.HrWorksClient;
import com.aoe.hrworks.HrWorksClientBuilder;
import com.sendgrid.SendGrid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Validated
@Configuration
@ConfigurationProperties(prefix = "mail")
@Getter
@Setter
public class MailConfigurationProperties {

    @NotNull(message = "mail api key shouldn't be null")
    @NotEmpty(message = "mail api key shouldn't be empty")
    private String apiKey;

    @Bean
    public SendGrid sendGrid(MailConfigurationProperties properties) {
        return new SendGrid(properties.getApiKey());
    }

}
