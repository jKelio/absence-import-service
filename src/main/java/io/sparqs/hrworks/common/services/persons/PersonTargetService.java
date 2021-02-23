package io.sparqs.hrworks.common.services.persons;

import io.sparqs.hrworks.config.MocoConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@Service
public class PersonTargetService {

    public static final String AUTHORIZATION = "Authorization";
    public static final String API_KEY_PREFIX = "Token token=";
    private final RestTemplate restTemplate;

    PersonTargetService(MocoConfigurationProperties properties, RestTemplateBuilder builder) {
        String baseUrl = properties.getBaseUrl();
        this.restTemplate = builder
                .defaultHeader(AUTHORIZATION, API_KEY_PREFIX + properties.getApiKey())
                .rootUri(baseUrl)
                .build();
    }


    public Collection<PersonEntity> getUsers() {
        waitHalfSecond();
        return Arrays.asList(Objects.requireNonNull(restTemplate
                .getForEntity("/users", PersonEntity[].class).getBody()));
    }

    public PersonEntity getUser(String id) {
        waitHalfSecond();
        return restTemplate.getForEntity("/users/{id}", PersonEntity.class, id).getBody();
    }

    private void waitHalfSecond() {
        synchronized (this) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                throw new RestClientException(e.getMessage(), e);
            }
        }
    }
}
