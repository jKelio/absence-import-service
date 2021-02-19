package io.sparqs.hrworks.api.absences;

import com.aoe.hrworks.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class AbsenceService {

    private final HrWorksClient client;

    AbsenceService(HrWorksClient client) {
        this.client = client;
    }

    public AbsenceTypeList getAllAbsenceTypes() {
        return client.getAllAbsenceTypes().blockingGet();
    }

    public Map<String, List<AbsenceData>> getAbsences(GetAbsencesRq payload) {
        return client.getAbsences(payload).blockingGet();
    }

    public Map<String, List<AbsenceDay>> getAbsencesInDays(GetAbsencesRq payload) {
        return getAbsences(payload).entrySet().stream()
                .flatMap(e -> e.getValue()
                        .stream().map(AbsenceData::getAbsences)
                        .flatMap(Collection::stream)
                        .map(this::splitIntoAbsenceDays)
                        .flatMap(Collection::stream)
                        .map(absenceDay -> addPersonnelNumberToAbsenceDay(e.getKey(), absenceDay)))
                .collect(groupingBy(AbsenceDay::getPersonnelNumber));
    }

    private AbsenceDay addPersonnelNumberToAbsenceDay(String personnelNumber, AbsenceDay day) {
        return day.toBuilder()
                .personnelNumber(personnelNumber)
                .build();
    }

    private List<AbsenceDay> splitIntoAbsenceDays(Absence absencePeriods) {
        final LocalDate startDate = convertDate(absencePeriods.getBeginDate());
        final LocalDate endDate = convertDate(absencePeriods.getEndDate());
        return startDate.datesUntil(endDate).parallel()
                .map(currentDate -> AbsenceDay.builder()
                        .name(absencePeriods.getName())
                        .type(findAbsenceTypeKey(absencePeriods.getName()))
                        .date(currentDate)
                        .am(true)
                        .pm(true)
                        .build())
                .collect(Collectors.toList());
    }

    private LocalDate convertDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private String findAbsenceTypeKey(String absenceTypeName) {
        return getAllAbsenceTypes().getAbsenceTypes().stream()
                .filter(t -> t.getName().equals(absenceTypeName))
                .findAny()
                .orElseThrow()
                .getKey();
    }
}
