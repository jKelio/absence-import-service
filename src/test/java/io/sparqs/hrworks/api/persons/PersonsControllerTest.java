package io.sparqs.hrworks.api.persons;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static io.sparqs.hrworks.api.persons.PersonServiceTest.PERSONNEL_NUMBER;
import static io.sparqs.hrworks.api.persons.PersonServiceTest.loadExpectedPersons;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PersonsControllerTest {

    private PersonsController controller;
    private PersonService service;

    @BeforeEach
    void setUp() {
        service = mock(PersonService.class);
        controller = new PersonsController(service);
    }

    @AfterEach
    void tearDown() {
        service = null;
        controller = null;
    }

    @Test
    public void testGetAllActivePersons() throws IOException {
        when(service.getAllActivePersons()).thenReturn(loadExpectedPersons());
        controller.getAllActivePersons();
        assertEquals(HttpStatus.OK, controller.getAllActivePersons().getStatusCode());
    }

    @Test
    public void testGetActivePerson() throws IOException {
        when(service.getActivePerson(anyString())).thenReturn(loadExpectedPersons().stream().findAny().orElseThrow());
        controller.getActivePerson(PERSONNEL_NUMBER);
        assertEquals(HttpStatus.OK, controller.getAllActivePersons().getStatusCode());
    }
}