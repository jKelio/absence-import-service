package io.sparqs.hrworks.config;

import com.sendgrid.SendGrid;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailConfiguration {

    @Bean
    public SendGrid sendGrid(MailConfigurationProperties properties) {
        return new SendGrid(properties.getApiKey());
    }

}
