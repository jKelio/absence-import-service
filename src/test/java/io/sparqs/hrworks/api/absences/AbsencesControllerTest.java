package io.sparqs.hrworks.api.absences;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static io.sparqs.hrworks.api.absences.AbsenceServiceTest.PAYLOAD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AbsencesControllerTest {

    private AbsencesController controller;
    private AbsenceService service;

    @BeforeEach
    void setUp() {
        service = mock(AbsenceService.class);
        controller = new AbsencesController(service);
    }

    @AfterEach
    void tearDown() {
        service = null;
        controller = null;
    }

    @Test
    public void testGetAbsenceTypes() throws IOException {
        when(service.getAllAbsenceTypes()).thenReturn(AbsenceServiceTest.loadAbsenceTypeList());
        assertEquals(HttpStatus.OK, controller.getAbsenceTypes().getStatusCode());
    }

    @Test
    public void testGetAbsences() throws IOException {
        when(service.getAbsences(eq(PAYLOAD))).thenReturn(AbsenceServiceTest.loadAbsenceData());
        assertEquals(HttpStatus.OK, controller.getAbsences(
                PAYLOAD.getBeginDate(),
                PAYLOAD.getEndDate(),
                PAYLOAD.getIdOrPersonnelNumberList()
        ).getStatusCode());
    }
}