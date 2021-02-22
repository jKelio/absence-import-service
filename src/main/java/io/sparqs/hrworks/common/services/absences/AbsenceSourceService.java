package io.sparqs.hrworks.common.services.absences;

import com.aoe.hrworks.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static io.sparqs.hrworks.common.services.absences.AbsenceTypeEnum.SICKNESS;
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
                        .filter(this::isConfirmedAbsence)
                        .map(this::splitIntoAbsenceDays)
                        .flatMap(Collection::stream)
                        .map(absenceDayEntity -> addPersonnelNumberToAbsenceDay(e.getKey(), absenceDayEntity)))
                .collect(groupingBy(AbsenceDayEntity::getPersonnelNumber));
    }

    private boolean isConfirmedAbsence(Absence a) {
        try {
            AbsenceTypeEnum type = AbsenceTypeEnum.fromSource(a.getName());
            return type.equals(SICKNESS) || type.getStatus().equals(a.getStatus());
        } catch(Exception exception) {
            return false;
        }
    }

    private AbsenceDayEntity addPersonnelNumberToAbsenceDay(String personnelNumber, AbsenceDayEntity day) {
        return day.toBuilder()
                .personnelNumber(personnelNumber)
                .build();
    }

    // TODO: clarify and adjust for half-day absences in time ranges more than one day.
    private List<AbsenceDayEntity> splitIntoAbsenceDays(Absence absencePeriods) {
        final LocalDate startDate = convertDate(absencePeriods.getBeginDate());
        final LocalDate endDate = convertDate(absencePeriods.getEndDate());
        final BigDecimal workingDays = new BigDecimal(absencePeriods.getWorkingDays());
        return startDate.datesUntil(endDate.plusDays(1)).parallel()
                .map(currentDate -> AbsenceDayEntity.builder()
                        .name(AbsenceTypeEnum.fromSource(absencePeriods.getName()))
                        .type(findAbsenceTypeKey(absencePeriods.getName()))
                        .date(currentDate)
                        .am(!workingDays.equals(new BigDecimal("0.5")) || !absencePeriods.isForenoonHalfDay())
                        .pm(!workingDays.equals(new BigDecimal("0.5")) || !absencePeriods.isAfternoonHalfDay())
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
