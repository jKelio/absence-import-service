package io.sparqs.hrworks.common.services.absences;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sparqs.hrworks.common.services.waiting.WaitingService;
import io.sparqs.hrworks.config.MocoConfigurationProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collection;

import static io.sparqs.hrworks.common.services.persons.PersonTargetService.API_KEY_PREFIX;
import static io.sparqs.hrworks.common.services.persons.PersonTargetService.AUTHORIZATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AbsenceTargetServiceTest {

    public static final String MOCKS_TARGET = "mocks/target/";
    public static final LocalDate BEGIN_DATE = LocalDate.parse("2021-01-01");
    public static final LocalDate END_DATE = LocalDate.parse("2021-12-31");
    public static final String USER_ID = "933611238";

    private AbsenceTargetService service;
    private RestTemplateBuilder builder;
    private RestTemplate rest;
    private MocoConfigurationProperties properties;
    private WaitingService waitingService;

    @BeforeEach
    void setUp() {
        builder = mock(RestTemplateBuilder.class);
        rest = mock(RestTemplate.class);
        properties = new MocoConfigurationProperties();
        properties.setApiKey("some-mocked-api-key");
        properties.setBaseUrl("https://mocoapp.com/api/v1");
        waitingService = mock(WaitingService.class);
        when(builder.defaultHeader(eq(AUTHORIZATION), eq(API_KEY_PREFIX + properties.getApiKey())))
                .thenReturn(builder);
        when(builder.rootUri(eq(properties.getBaseUrl())))
                .thenReturn(builder);
        when(builder.build()).thenReturn(rest);
        doNothing().when(waitingService).waitSecond();
        service = new AbsenceTargetService(properties, builder, waitingService);
    }

    @AfterEach
    void tearDown() {
        builder = null;
        rest = null;
        properties = null;
        service = null;
    }

    @Test
    void testGetSchedulesByTimeRangeAndUserId() throws IOException {
        ResponseEntity<AbsenceDayEntity[]> response = ResponseEntity
                .ok(loadAbsenceDays("absences_by_userid").toArray(new AbsenceDayEntity[0]));
        doReturn(response).when(rest).getForEntity(
                "/schedules?from={beginDate}&to={endDate}&user_id={userId}",
                AbsenceDayEntity[].class,
                BEGIN_DATE.toString(),
                END_DATE.toString(),
                USER_ID
        );
        Collection<AbsenceDayEntity> absenceDays = service
                 .readSchedules(LocalDate.parse("2021-01-01"), LocalDate.parse("2021-12-31"), "933611238");
        assertEquals(19, absenceDays.size());
    }

    @Test
    void testGetSchedulesByTimeRange() throws IOException {
        ResponseEntity<AbsenceDayEntity[]> response = ResponseEntity
                .ok(loadAbsenceDays("absences").toArray(new AbsenceDayEntity[0]));
        doReturn(response).when(rest).getForEntity(
                "/schedules?from={beginDate}&to={endDate}",
                AbsenceDayEntity[].class,
                BEGIN_DATE.toString(),
                END_DATE.toString()
        );
        Collection<AbsenceDayEntity> absenceDays = service
                .readSchedules(BEGIN_DATE, END_DATE);
        assertEquals(100, absenceDays.size());
    }

    @Test
    void testGetScheduleById() throws IOException {
        final long scheduleId = 4298189;
        ResponseEntity<AbsenceDayEntity> response = ResponseEntity.ok(loadAbsenceDay("absence"));
        doReturn(response).when(rest).getForEntity(
                eq("/schedules/{scheduleId}"),
                eq(AbsenceDayEntity.class),
                eq(scheduleId)
        );
        AbsenceDayEntity absenceDay = service.readSchedule(scheduleId);
        assertEquals(scheduleId, absenceDay.getId());
    }

    @Test
    void testCreateSchedule() throws IOException {
        ResponseEntity<AbsenceDayEntity> response = ResponseEntity.ok(loadAbsenceDay("created_absence"));
        when(rest.postForEntity(eq("/schedules"), any(HttpEntity.class), eq(AbsenceDayEntity.class)))
                .thenReturn(response);
        AbsenceDayEntity entity = service.createSchedule(AbsenceDayEntity.builder()
                .date(LocalDate.parse("2021-02-22"))
                .name(AbsenceTypeEnum.VACATION)
                .am(true)
                .pm(true)
                .userId(933611238)
                .comment("automatically imported from HRworks")
                .build());
        assertEquals(LocalDate.parse("2021-02-22"), entity.getDate());
    }

    @Test
    void testDeleteSchedule() {
        doNothing().when(rest).delete(eq("/schedules/{scheduleId}"), eq("4298351"));
        service.deleteSchedule(4298351L);
    }

    private Collection<AbsenceDayEntity> loadAbsenceDays(String fileName) throws IOException {
        InputStream is = AbsenceTargetServiceTest.class.getClassLoader()
                .getResourceAsStream(MOCKS_TARGET + fileName  + ".json");
        return new ObjectMapper().readValue(is, new TypeReference<>() {});
    }

    private AbsenceDayEntity loadAbsenceDay(String fileName) throws IOException {
        InputStream is = AbsenceTargetServiceTest.class.getClassLoader()
                .getResourceAsStream(MOCKS_TARGET + fileName  + ".json");
        return new ObjectMapper().readValue(is, new TypeReference<>() {});
    }
}