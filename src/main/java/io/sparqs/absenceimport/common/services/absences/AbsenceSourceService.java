package io.sparqs.absenceimport.common.services.absences;

import com.aoe.hrworks.*;
import io.sparqs.absenceimport.common.services.absences.AbsenceDayEntity.AbsenceDayEntityBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.sparqs.absenceimport.common.services.absences.AbsenceTypeEnum.SICKNESS_WITH_CERTIFICATE;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.util.stream.Collectors.groupingBy;

@Service
public class AbsenceSourceService {

    public static final String COUNTRY_CODE = "DEU";
    public static final String STATE_NAME = "North Rhine-Westfalia";
    private final Logger logger = LoggerFactory.getLogger(AbsenceSourceService.class);
    private final HrWorksClient client;

    AbsenceSourceService(HrWorksClient client) {
        this.client = client;
    }

    public AbsenceTypeList getAllAbsenceTypes() {
        logger.info("get all absence types");
        return client.getAllAbsenceTypes().blockingGet();
    }

    public Collection<Holiday> getHolidays(int year) {
        logger.info("get all holidays for year {}", year);
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
        logger.info("get all absences between {} and {} for {} persons",
                payload.getBeginDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(ISO_DATE),
                payload.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(ISO_DATE),
                payload.getIdOrPersonnelNumberList().size());
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
                        .map(absenceDayEntity -> addPersonIdToAbsenceDay(e.getKey(), absenceDayEntity)))
                .collect(groupingBy(AbsenceDayEntity::getPersonId));
    }

    private boolean isConfirmedAbsence(Absence a) {
        try {
            AbsenceTypeEnum type = AbsenceTypeEnum.fromSource(a.getName());
            return type.equals(SICKNESS_WITH_CERTIFICATE) || type.getStatus().equals(a.getStatus());
        } catch(Exception exception) {
            return false;
        }
    }

    private AbsenceDayEntity addPersonIdToAbsenceDay(String personnelNumber, AbsenceDayEntity day) {
        return day.toBuilder()
                .personId(personnelNumber)
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

        builder.am(!(absencePeriods.isForenoonHalfDay() && currentDate.isEqual(startDate)));
        builder.pm(!(absencePeriods.isAfternoonHalfDay() && currentDate.isEqual(endDate)));

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
