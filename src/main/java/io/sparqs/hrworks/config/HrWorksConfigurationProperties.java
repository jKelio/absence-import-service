package io.sparqs.hrworks.config;

import com.aoe.hrworks.HrWorksClient;
import com.aoe.hrworks.HrWorksClientBuilder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "hrworks")
public class HrWorksConfigurationProperties {

    @Getter
    @Setter
    @NotNull(message = "public access key shouldn't be null")
    @NotEmpty(message = "public access key shouldn't be empty")
    private String publicAccessKey;

    @Getter
    @Setter
    @NotNull(message = "private access key shouldn't be null")
    @NotEmpty(message = "private access key shouldn't be empty")
    private String privateAccessKey;

}
