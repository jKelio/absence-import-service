package io.sparqs.hrworks.common.services.absences;

import com.aoe.hrworks.*;
import io.sparqs.hrworks.common.services.absences.AbsenceDayEntity.AbsenceDayEntityBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static io.sparqs.hrworks.common.services.absences.AbsenceTypeEnum.SICKNESS;
import static java.util.stream.Collectors.groupingBy;

@Service
public class AbsenceSourceService {

    public static final String COUNTRY_CODE = "DEU";
    public static final String STATE_NAME = "North Rhine-Westfalia";
    private final HrWorksClient client;

    AbsenceSourceService(HrWorksClient client) {
        this.client = client;
    }

    public AbsenceTypeList getAllAbsenceTypes() {
        return client.getAllAbsenceTypes().blockingGet();
    }

    public Collection<Holiday> getHolidays(int year) {
        Map<String, HolidayData> holidayDataMap = client
                .getHolidays(new GetHolidaysRq(year, null, null)).blockingGet();
        HolidayData countryHolidayData = holidayDataMap.get(COUNTRY_CODE);
        Collection<Holiday> holidays = countryHolidayData.getGeneralHolidays();
        holidays.addAll(countryHolidayData.getStateHolidays().get(STATE_NAME));
        holidays.addAll(countryHolidayData.getPermamentEstablishmentHolidays().values().stream()
                .flatMap(Collection::stream).collect(Collectors.toList()));
        return holidays;
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

    private List<AbsenceDayEntity> splitIntoAbsenceDays(Absence absencePeriods) {
        final LocalDate startDate = convertDate(absencePeriods.getBeginDate());
        final LocalDate endDate = convertDate(absencePeriods.getEndDate());
        return startDate.datesUntil(endDate.plusDays(1)).parallel()
                .map(currentDate -> buildAbsenceDay(currentDate, absencePeriods))
                .collect(Collectors.toList());
    }

    private AbsenceDayEntity buildAbsenceDay(LocalDate currentDate, Absence absencePeriods) {
        final LocalDate startDate = convertDate(absencePeriods.getBeginDate());
        final LocalDate endDate = convertDate(absencePeriods.getEndDate());
        final AbsenceDayEntityBuilder builder = AbsenceDayEntity.builder()
                .name(AbsenceTypeEnum.fromSource(absencePeriods.getName()))
                .type(findAbsenceTypeKey(absencePeriods.getName()))
                .date(currentDate);

        if (absencePeriods.isForenoonHalfDay() || absencePeriods.isAfternoonHalfDay()) {
            builder.am(!(absencePeriods.isForenoonHalfDay() && (currentDate.isEqual(startDate) || currentDate.isEqual(endDate))));
            builder.pm(!(absencePeriods.isAfternoonHalfDay() && (currentDate.isEqual(startDate) || currentDate.isEqual(endDate))));
        } else {
            builder.am(true);
            builder.pm(true);
        }

        return builder.build();
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
