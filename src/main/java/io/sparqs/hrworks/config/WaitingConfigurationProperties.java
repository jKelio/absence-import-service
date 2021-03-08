package io.sparqs.hrworks.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "waiting")
public class WaitingConfigurationProperties {

    @Getter
    @Setter
    @NotNull(message = "timeout shouldn't be null")
    @Min(0)
    @Max(Integer.MAX_VALUE)
    private int timeout;

}
