package io.sparqs.hrworks.common.services.waiting;

import io.sparqs.hrworks.config.WaitingConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;


@Service
public class WaitingService {

    private final Logger logger = LoggerFactory.getLogger(WaitingService.class);
    private final WaitingConfigurationProperties properties;

    WaitingService(WaitingConfigurationProperties properties) {
        this.properties = properties;
    }

    public void waitConfiguredTime() {
        synchronized (this) {
            try {
                wait(properties.getTimeout());
            } catch (InterruptedException e) {
                logger.error("waiting is interrupted", e);
                throw new RestClientException(e.getMessage(), e);
            }
        }
    }

}
