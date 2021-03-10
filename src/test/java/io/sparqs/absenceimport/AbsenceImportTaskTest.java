package io.sparqs.absenceimport;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SpringBootTest
class AbsenceImportTaskTest {

    @Autowired
    AbsenceImportTask task;

    @Test
    void testCleanAndImportAbsences() {
        task.cleanAndImportAbsences();
        task.join();
    }
}