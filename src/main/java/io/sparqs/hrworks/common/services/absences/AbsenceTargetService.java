package io.sparqs.hrworks.common.services.absences;

import io.sparqs.hrworks.config.MocoConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@Service
public class AbsenceTargetService {

    public static final String AUTHORIZATION = "Authorization";
    public static final String API_KEY_PREFIX = "Token token=";
    private final RestTemplate restTemplate;

    AbsenceTargetService(MocoConfigurationProperties properties, RestTemplateBuilder builder) {
        String baseUrl = properties.getBaseUrl();
        this.restTemplate = builder
                .defaultHeader(AUTHORIZATION, API_KEY_PREFIX + properties.getApiKey())
                .rootUri(baseUrl)
                .build();
    }

    public AbsenceDayEntity createSchedule(AbsenceDayEntity entity) throws RestClientException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AbsenceDayEntity> httpEntity = new HttpEntity<>(entity, headers);
        waitSecond();
        return restTemplate.postForEntity(
                "/schedules",
                httpEntity,
                AbsenceDayEntity.class
        ).getBody();
    }

    public Collection<AbsenceDayEntity> readSchedules(LocalDate beginDate, LocalDate endDate, String userId) throws RestClientException {
        waitSecond();
        ResponseEntity<AbsenceDayEntity[]> response = restTemplate.getForEntity(
                "/schedules?from={beginDate}&to={endDate}&user_id={userId}",
                AbsenceDayEntity[].class,
                beginDate.toString(),
                endDate.toString(),
                userId
        );
        return Arrays.asList(Objects.requireNonNull(response.getBody()));
    }

    public Collection<AbsenceDayEntity> readSchedules(LocalDate beginDate, LocalDate endDate) throws RestClientException {
        waitSecond();
        ResponseEntity<AbsenceDayEntity[]> response = restTemplate.getForEntity(
                "/schedules?from={beginDate}&to={endDate}",
                AbsenceDayEntity[].class,
                beginDate.toString(),
                endDate.toString()
        );
        return Arrays.asList(Objects.requireNonNull(response.getBody()));
    }

    public AbsenceDayEntity readSchedule(Long scheduleId) throws RestClientException {
        waitSecond();
        ResponseEntity<AbsenceDayEntity> response = restTemplate.getForEntity(
                "/schedules/{scheduleId}",
                AbsenceDayEntity.class,
                scheduleId
        );
        return response.getBody();
    }

    public void deleteSchedule(Long scheduleId) throws RestClientException {
        waitSecond();
        restTemplate.delete("/schedules/{scheduleId}", scheduleId);
    }

    private void waitSecond() {
        synchronized (this) {
            try {
                wait(500); // TODO: make configurable, bitch!
            } catch (InterruptedException e) {
                throw new RestClientException(e.getMessage(), e);
            }
        }
    }
}
