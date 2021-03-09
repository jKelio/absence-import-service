package io.sparqs.hrworks.common.services.persons;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sparqs.hrworks.common.services.waiting.WaitingService;
import io.sparqs.hrworks.config.MocoConfigurationProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import static io.sparqs.hrworks.common.services.persons.PersonTargetService.API_KEY_PREFIX;
import static io.sparqs.hrworks.common.services.persons.PersonTargetService.AUTHORIZATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


class PersonTargetServiceTest {

    public static final String MOCKS_TARGET = "mocks/target/";
    public static final String USER_ID = "933610310";
    private PersonTargetService personService;
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
        doNothing().when(waitingService).waitConfiguredTime();
        personService = new PersonTargetService(properties, builder, waitingService);
    }

    @AfterEach
    void tearDown() {
        builder = null;
        rest = null;
        properties = null;
        personService = null;
        waitingService= null;
    }

    @Test
    public void testGetPersons() throws IOException {
        ResponseEntity<PersonEntity[]> response = ResponseEntity
                .ok(loadMockedPersons().toArray(new PersonEntity[0]));
        doReturn(response).when(rest)
                .getForEntity("/users", PersonEntity[].class);
        Collection<PersonEntity> persons = personService.getUsers();
        assertEquals(9, persons.size());
    }

    @Test
    public void testGetPerson() throws IOException {
        ResponseEntity<PersonEntity> response = ResponseEntity.ok(loadMockedPerson());
        doReturn(response).when(rest).getForEntity("/users/{id}", PersonEntity.class, USER_ID);
        PersonEntity person = personService.getUser(USER_ID);
        assertEquals(USER_ID, person.getId());
    }

    private Collection<PersonEntity> loadMockedPersons() throws IOException {
        InputStream is = PersonTargetServiceTest.class.getClassLoader()
                .getResourceAsStream(MOCKS_TARGET + "persons.json");
        return new ObjectMapper().readValue(is, new TypeReference<>() {
        });
    }

    private PersonEntity loadMockedPerson() throws IOException {
        InputStream is = PersonTargetServiceTest.class.getClassLoader()
                .getResourceAsStream(MOCKS_TARGET + "person.json");
        return new ObjectMapper().readValue(is, new TypeReference<>() {
        });
    }

}