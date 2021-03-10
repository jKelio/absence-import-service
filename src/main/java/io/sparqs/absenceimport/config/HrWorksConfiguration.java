package io.sparqs.absenceimport.config;

import com.aoe.hrworks.HrWorksClient;
import com.aoe.hrworks.HrWorksClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HrWorksConfiguration {

    @Bean
    public HrWorksClientBuilder clientBuilder() {
        return HrWorksClientBuilder.INSTANCE;
    }

    @Bean
    public HrWorksClient client(HrWorksConfigurationProperties properties, HrWorksClientBuilder clientBuilder) {
        return clientBuilder.buildClient(properties.getPublicAccessKey(), properties.getPrivateAccessKey());
    }


}
