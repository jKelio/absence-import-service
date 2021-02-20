package io.sparqs.hrworks.common.services.absences;

import com.aoe.hrworks.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class AbsenceSourceService {

    private final HrWorksClient client;

    AbsenceSourceService(HrWorksClient client) {
        this.client = client;
    }

    public AbsenceTypeList getAllAbsenceTypes() {
        return client.getAllAbsenceTypes().blockingGet();
    }

    public Map<String, List<AbsenceData>> getAbsences(GetAbsencesRq payload) {
        return client.getAbsences(payload).blockingGet();
    }

    public Map<String, List<AbsenceDayEntity>> getAbsencesInDays(GetAbsencesRq payload) {
        return getAbsences(payload).entrySet().stream()
                .flatMap(e -> e.getValue()
                        .stream().map(AbsenceData::getAbsences)
                        .flatMap(Collection::stream)
                        .map(this::splitIntoAbsenceDays)
                        .flatMap(Collection::stream)
                        .map(absenceDayEntity -> addPersonnelNumberToAbsenceDay(e.getKey(), absenceDayEntity)))
                .collect(groupingBy(AbsenceDayEntity::getPersonnelNumber));
    }

    private AbsenceDayEntity addPersonnelNumberToAbsenceDay(String personnelNumber, AbsenceDayEntity day) {
        return day.toBuilder()
                .personnelNumber(personnelNumber)
                .build();
    }

    private List<AbsenceDayEntity> splitIntoAbsenceDays(Absence absencePeriods) {
        final LocalDate startDate = convertDate(absencePeriods.getBeginDate());
        final LocalDate endDate = convertDate(absencePeriods.getEndDate());
        return startDate.datesUntil(endDate).parallel()
                .map(currentDate -> AbsenceDayEntity.builder()
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
