package io.sparqs.hrworks.common.services.absences;

import com.aoe.hrworks.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.internal.LinkedTreeMap;
import io.reactivex.Single;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static io.sparqs.hrworks.common.services.persons.PersonSourceServiceTest.PERSONNEL_NUMBER;
import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AbsenceSourceServiceTest {

    private AbsenceSourceService service;
    private HrWorksClient client;
    public static final GetAbsencesRq PAYLOAD = new GetAbsencesRq(
            Date.from(LocalDate.parse("2020-01-01", DateTimeFormatter.ISO_DATE).atStartOfDay().toInstant(ZoneOffset.UTC)),
            Date.from(LocalDate.parse("2020-12-31", DateTimeFormatter.ISO_DATE).atStartOfDay().toInstant(ZoneOffset.UTC)),
            Collections.singletonList(PERSONNEL_NUMBER),
            null,
            true
    );

    @BeforeEach
    void setUp() {
        client = mock(HrWorksClient.class);
        service = new AbsenceSourceService(client);
    }

    @AfterEach
    void tearDown() {
        client = null;
        service = null;
    }

    @Test
    public void testGetAllAbsenceTypes() throws IOException {
        AbsenceTypeList absenceTypes = loadAbsenceTypeList();
        when(client.getAllAbsenceTypes()).thenReturn(Single.just(absenceTypes));
        assertEquals(absenceTypes.getAbsenceTypes().size(), service.getAllAbsenceTypes().getAbsenceTypes().size());
    }

    @Test
    public void testGetAbsences() throws IOException {
        Map<String, List<AbsenceData>> mockedAbsenceData = loadAbsenceData();
        when(client.getAbsences(any(GetAbsencesRq.class))).thenReturn(Single.just(mockedAbsenceData));
        Map<String, List<AbsenceData>> absences = service.getAbsences(PAYLOAD);
        assertEquals(mockedAbsenceData.size(), absences.size());
        assertEquals(mockedAbsenceData.get(PERSONNEL_NUMBER).size(), absences.get(PERSONNEL_NUMBER).size());
    }

    @Test
    public void testGetAbsencesInDays() throws IOException {
        final AbsenceTypeList absenceTypes = loadAbsenceTypeList();
        final Map<String, List<AbsenceData>> mockedAbsenceData = loadAbsenceData();
        when(client.getAllAbsenceTypes()).thenReturn(Single.just(absenceTypes));
        when(client.getAbsences(eq(PAYLOAD))).thenReturn(Single.just(mockedAbsenceData));
        Map<String, List<AbsenceDayEntity>> absencesInDays = service.getAbsencesInDays(PAYLOAD);
        assertEquals(39, absencesInDays.get(PERSONNEL_NUMBER).size());
        assertTrue(absencesInDays.get(PERSONNEL_NUMBER).get(0).isAm());
        assertTrue(absencesInDays.get(PERSONNEL_NUMBER).get(0).isPm());
        assertNull(absencesInDays.get(PERSONNEL_NUMBER).get(0).getId());
        assertNotNull(absencesInDays.get(PERSONNEL_NUMBER).get(0).getName());
        assertNotNull(absencesInDays.get(PERSONNEL_NUMBER).get(0).getType());
        assertNotNull(absencesInDays.get(PERSONNEL_NUMBER).get(0).getDate());
    }

    @Test
    public void testGetHolidays() throws IOException {
        final Map<String, HolidayData> holidayData = loadHolidayDataMap();
        when(client.getHolidays(any(GetHolidaysRq.class))).thenReturn(Single.just(holidayData));
        Collection<Holiday> holidays = service.getHolidays(2021);
        assertEquals(13, holidays.size());
    }

    private static Map<String, HolidayData> loadHolidayDataMap() throws IOException {
        InputStream is = AbsenceSourceServiceTest.class.getClassLoader()
                .getResourceAsStream("mocks/source/holidays.json");
        Map<String, TransferHolidayData> map = new ObjectMapper().readValue(is, new TypeReference<>() {});
        return map.entrySet().stream()
                .map(e -> new HolidayDataTuple(e.getKey(), e.getValue().getHolidayData()))
                .collect(toMap(HolidayDataTuple::getKey, HolidayDataTuple::getHolidayData));
    }


    public static AbsenceTypeList loadAbsenceTypeList() throws IOException {
        InputStream is = AbsenceSourceServiceTest.class.getClassLoader()
                .getResourceAsStream("mocks/source/absencetypes.json");
        return new ObjectMapper().readValue(is, TransferAbsenceTypeList.class)
                .getAbsenceTypeList();
    }

    public static Map<String, List<AbsenceData>> loadAbsenceData() throws IOException {
        InputStream is = AbsenceSourceServiceTest.class.getClassLoader()
                .getResourceAsStream("mocks/source/absences.json");
        Map<String, List<TransferAbsenceData>> transferAbsences = new ObjectMapper()
                .readValue(is, new TypeReference<>() {});
        Map<String, List<AbsenceData>>  absenceData = new LinkedTreeMap<>();
        transferAbsences.keySet()
                .forEach(p -> absenceData.put(p, transferAbsences.get(p).stream()
                        .map(TransferAbsenceData::getAbsenceData).collect(Collectors.toList())));
        return absenceData;
    }

    private static String findAbsenceTypeKey(String absenceTypeName) {
        try {
            return loadAbsenceTypeList().getAbsenceTypes().stream()
                    .filter(t -> t.getName().equals(absenceTypeName))
                    .findAny()
                    .orElseThrow()
                    .getKey();
        } catch (IOException e) {
            throw new NoSuchElementException();
        }
    }

    @Getter
    private static class TransferAbsenceTypeList {
        private final List<TransferAbsenceType> transferAbsenceTypes;
        private final AbsenceTypeList absenceTypeList;

        @JsonCreator
        TransferAbsenceTypeList(@JsonProperty("absenceTypes") List<TransferAbsenceType> absenceTypes) {
            transferAbsenceTypes = absenceTypes;
            absenceTypeList = new AbsenceTypeList(absenceTypes.stream()
                    .map(TransferAbsenceType::getAbsenceType).collect(Collectors.toList()));
        }
    }

    @Getter
    private static class TransferAbsenceType {
        private final AbsenceType absenceType;

        @JsonCreator
        TransferAbsenceType(
                @JsonProperty("name") String name,
                @JsonProperty("key") String key,
                @JsonProperty("type") String type,
                @JsonProperty("active") boolean isActive,
                @JsonProperty("reducesHolidayEntitlement") boolean reducesHolidayEntitlement) {
            absenceType = new AbsenceType(name, key, type, isActive, reducesHolidayEntitlement);
        }
    }

    @Getter
    private static class HolidayDataTuple {
        private final String key;
        private final HolidayData holidayData;

        HolidayDataTuple(String key, HolidayData holidayData) {
            this.key = key;
            this.holidayData = holidayData;
        }
    }

    @Getter
    private static class TransferHolidayData {
        private final HolidayData holidayData;

        @JsonCreator
        TransferHolidayData(
                @JsonProperty("permamentEstablishmentHolidays") Map<String, List<TransferHoliday>> permanentEstablishmentHolidays,
                @JsonProperty("stateHolidays") Map<String, List<TransferHoliday>> stateHolidays,
                @JsonProperty("generalHolidays") List<TransferHoliday> generalHolidays
        ) {
            holidayData = new HolidayData(
                    map(permanentEstablishmentHolidays),
                    map(stateHolidays),
                    generalHolidays.stream().map(TransferHolidayData::map).collect(Collectors.toList())
            );
        }

        private static Map<String, List<Holiday>> map(Map<String, List<TransferHoliday>> groupedHolidays) {
            return groupedHolidays.entrySet().stream()
                    .flatMap(e -> e.getValue().stream().map(t -> new TransferHoliday(e.getKey(), t.getHoliday())))
                    .collect(groupingBy(TransferHoliday::getKey, mapping(TransferHoliday::getHoliday, toList())));
        }

        private static Holiday map(TransferHoliday holiday) {
            return holiday.getHoliday();
        }
    }

    @Getter
    private static class TransferHoliday{
        private final Holiday holiday;

        @Nullable
        private String key;

        @JsonCreator
        TransferHoliday(
                @JsonProperty("date") Date date,
                @JsonProperty("halfDay") boolean halfday,
                @JsonProperty("name") String name
        ) {
            holiday = new Holiday(date, halfday, name);
        }

        TransferHoliday(String key, Holiday holiday) {
            this.key = key;
            this.holiday = holiday;
        }
    }

    @Getter
    private static class TransferAbsenceData {
        private final AbsenceData absenceData;

        @JsonCreator
        TransferAbsenceData(
            @JsonProperty("beginDate") Date from,
            @JsonProperty("endDate") Date to,
            @JsonProperty("absences") List<TransferAbsence> absences
        ) {
            absenceData = new AbsenceData(from, to, absences.stream()
                    .map(TransferAbsence::getAbsence).collect(Collectors.toList()));
        }
    }

    @Getter
    private static class TransferAbsence {
        private final Absence absence;

        @JsonCreator
        TransferAbsence(
                @JsonProperty("name") String name,
                @JsonProperty("absenceTypeKey") String absenceTypeKey,
                @JsonProperty("beginDate") Date beginDate,
                @JsonProperty("endDate") Date endDate,
                @JsonProperty("status") String status,
                @JsonProperty("workingDays") String workingdays,
                @JsonProperty(value = "isForenoonHalfDay") boolean forenoonHalfDay,
                @JsonProperty("isAfternoonHalfDay") boolean afternoonHalfDay
        ) {
            absence = new Absence(
                    name,
                    findAbsenceTypeKey(name),
                    beginDate,
                    endDate,
                    status,
                    Objects.isNull(workingdays) ? "0.0" : workingdays,
                    forenoonHalfDay,
                    afternoonHalfDay
            );
        }
    }

}