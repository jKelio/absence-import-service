package io.sparqs.hrworks;

import com.aoe.hrworks.AbsenceData;
import com.aoe.hrworks.GetAbsencesRq;
import com.aoe.hrworks.Person;
import io.sparqs.hrworks.common.services.absences.AbsenceDayEntity;
import io.sparqs.hrworks.common.services.absences.AbsenceSourceService;
import io.sparqs.hrworks.common.services.absences.AbsenceTargetService;
import io.sparqs.hrworks.common.services.persons.PersonEntity;
import io.sparqs.hrworks.common.services.persons.PersonSourceService;
import io.sparqs.hrworks.common.services.persons.PersonTargetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class IntegrationTests {

    @Autowired
    PersonSourceService personSourceService;

    @Autowired
    PersonTargetService personTargetService;

    @Autowired
    AbsenceSourceService absenceSourceService;

    @Autowired
    AbsenceTargetService absenceTargetService;

    @Test
    void test() {
        List<Person> persons = personSourceService.getAllActivePersons();
        Map<String, List<AbsenceData>> days = absenceSourceService
                .getAbsences(new GetAbsencesRq(
                        convertLocalDate(LocalDate.parse("2021-02-01")),
                        convertLocalDate(LocalDate.parse("2021-02-20")),
                        persons.stream().map(Person::getPersonnelNumber).collect(Collectors.toList()),
                        null,
                        true
                ));
        assertNotNull(days);
    }

    private Date convertLocalDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

}
