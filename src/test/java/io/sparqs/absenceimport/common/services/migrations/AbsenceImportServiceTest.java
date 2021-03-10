package io.sparqs.absenceimport.common.services.migrations;

import io.sparqs.absenceimport.AbsenceImportService;
import io.sparqs.absenceimport.AbsenceImportTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.fail;

@Disabled
@SpringBootTest
class AbsenceImportServiceTest {

    public static final LocalDate BEGIN_DATE = LocalDate.parse("2021-01-01");
    public static final LocalDate END_DATE = LocalDate.parse("2021-12-31");

    @Autowired
    AbsenceImportService service;

    @Autowired
    AbsenceImportTask task;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testImportAbsenceDays() {
        try {
            task.interrupt();
            service.cleanAbsenceDays(BEGIN_DATE, END_DATE);
            service.importAbsenceDays(BEGIN_DATE, END_DATE);
        } catch(Exception e) {
            fail(e.getMessage(), e);
        }
    }
}