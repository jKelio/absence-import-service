package io.sparqs.hrworks.common.services.waiting;

import io.sparqs.hrworks.config.WaitingConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class WaitingService {

    private final WaitingConfigurationProperties properties;

    WaitingService(WaitingConfigurationProperties properties) {
        this.properties = properties;
    }

    public void waitSecond() {
        synchronized (this) {
            try {
                wait(properties.getTimeout());
            } catch (InterruptedException e) {
                throw new RestClientException(e.getMessage(), e);
            }
        }
    }

}
