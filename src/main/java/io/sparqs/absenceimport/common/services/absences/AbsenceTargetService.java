package io.sparqs.absenceimport.common.services.absences;

import io.sparqs.absenceimport.common.services.waiting.WaitingService;
import io.sparqs.absenceimport.config.MocoConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static java.time.format.DateTimeFormatter.ISO_DATE;

@Service
public class AbsenceTargetService {

    public static final String AUTHORIZATION = "Authorization";
    public static final String API_KEY_PREFIX = "Token token=";
    private final Logger logger = LoggerFactory.getLogger(AbsenceTargetService.class);
    private final RestTemplate restTemplate;
    private final WaitingService waitingService;

    AbsenceTargetService(MocoConfigurationProperties properties, RestTemplateBuilder builder, WaitingService waitingService) {
        String baseUrl = properties.getBaseUrl();
        this.restTemplate = builder
                .defaultHeader(AUTHORIZATION, API_KEY_PREFIX + properties.getApiKey())
                .rootUri(baseUrl)
                .build();
        this.waitingService = waitingService;
    }

    public AbsenceDayEntity createSchedule(AbsenceDayEntity entity) throws RestClientException {
        logger.info("create schedule of type {} for user {} {} ({})",
                entity.getName().toString(), entity.getUserFirstName(), entity.getUserLastName(), entity.getUserId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AbsenceDayEntity> httpEntity = new HttpEntity<>(entity, headers);
        waitingService.waitConfiguredTime();
        return restTemplate.postForEntity(
                "/schedules",
                httpEntity,
                AbsenceDayEntity.class
        ).getBody();
    }

    public Collection<AbsenceDayEntity> readSchedules(LocalDate beginDate, LocalDate endDate, String userId) throws RestClientException {
        logger.info("read schedules between {} and {} for user id {}",
                beginDate.format(ISO_DATE), endDate.format(ISO_DATE), userId);
        waitingService.waitConfiguredTime();
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
        logger.info("read schedules between {} and {}", beginDate.format(ISO_DATE), endDate.format(ISO_DATE));
        waitingService.waitConfiguredTime();
        ResponseEntity<AbsenceDayEntity[]> response = restTemplate.getForEntity(
                "/schedules?from={beginDate}&to={endDate}",
                AbsenceDayEntity[].class,
                beginDate.toString(),
                endDate.toString()
        );
        return Arrays.asList(Objects.requireNonNull(response.getBody()));
    }

    public AbsenceDayEntity readSchedule(Long scheduleId) throws RestClientException {
        logger.info("read schedule by schedule id {}", scheduleId);
        waitingService.waitConfiguredTime();
        ResponseEntity<AbsenceDayEntity> response = restTemplate.getForEntity(
                "/schedules/{scheduleId}",
                AbsenceDayEntity.class,
                scheduleId
        );
        return response.getBody();
    }

    public void deleteSchedule(Long scheduleId) throws RestClientException {
        logger.info("delete schedule by schedule id {}", scheduleId);
        waitingService.waitConfiguredTime();
        restTemplate.delete("/schedules/{scheduleId}", scheduleId);
    }

}
