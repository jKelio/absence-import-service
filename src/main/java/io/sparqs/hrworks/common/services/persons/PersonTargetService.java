package io.sparqs.hrworks.common.services.persons;

import io.sparqs.hrworks.common.services.waiting.WaitingService;
import io.sparqs.hrworks.config.MocoConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@Service
public class PersonTargetService {

    public static final String AUTHORIZATION = "Authorization";
    public static final String API_KEY_PREFIX = "Token token=";
    private final Logger logger = LoggerFactory.getLogger(PersonTargetService.class);
    private final String baseUrl;
    private final RestTemplate restTemplate;
    private final WaitingService waitingService;

    PersonTargetService(MocoConfigurationProperties properties, RestTemplateBuilder builder, WaitingService waitingService) {
        this.baseUrl = properties.getBaseUrl();
        this.restTemplate = builder
                .defaultHeader(AUTHORIZATION, API_KEY_PREFIX + properties.getApiKey())
                .rootUri(baseUrl)
                .build();
        this.waitingService = waitingService;
    }


    public Collection<PersonEntity> getUsers() {
        logger.info("get persons from target {}", baseUrl);
        waitingService.waitConfiguredTime();
        return Arrays.asList(Objects.requireNonNull(restTemplate
                .getForEntity("/users", PersonEntity[].class).getBody()));
    }

    public PersonEntity getUser(String id) {
        logger.info("get person {} from target {}", id, baseUrl);
        waitingService.waitConfiguredTime();
        return restTemplate.getForEntity("/users/{id}", PersonEntity.class, id).getBody();
    }

}
