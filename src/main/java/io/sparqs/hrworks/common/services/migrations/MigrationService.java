package io.sparqs.hrworks.common.services.migrations;

import com.aoe.hrworks.GetAbsencesRq;
import com.aoe.hrworks.Holiday;
import com.aoe.hrworks.Person;
import io.sparqs.hrworks.common.services.absences.AbsenceDayEntity;
import io.sparqs.hrworks.common.services.absences.AbsenceSourceService;
import io.sparqs.hrworks.common.services.absences.AbsenceTargetService;
import io.sparqs.hrworks.common.services.absences.AbsenceTypeEnum;
import io.sparqs.hrworks.common.services.persons.PersonEntity;
import io.sparqs.hrworks.common.services.persons.PersonSourceService;
import io.sparqs.hrworks.common.services.persons.PersonTargetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.sparqs.hrworks.common.services.absences.AbsenceTypeEnum.*;
import static java.lang.Integer.parseInt;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.util.stream.Collectors.groupingBy;

@Service
public class MigrationService {

    private final Logger logger = LoggerFactory.getLogger(MigrationService.class);
    private final PersonSourceService personSourceService;
    private final PersonTargetService personTargetService;
    private final AbsenceSourceService absenceSourceService;
    private final AbsenceTargetService absenceTargetService;


    MigrationService(
            PersonSourceService personSourceService,
            PersonTargetService personTargetService,
            AbsenceSourceService absenceSourceService,
            AbsenceTargetService absenceTargetService
    ) {
        this.personSourceService = personSourceService;
        this.personTargetService = personTargetService;
        this.absenceSourceService = absenceSourceService;
        this.absenceTargetService = absenceTargetService;
    }

    /**
     * TODO: add documentation
     * @param beginDate
     * @param endDate
     */
    private void cleanAbsenceDays(LocalDate beginDate, LocalDate endDate) {
        logger.info("cleaning all existing absence days from {} to {} at target started",
                beginDate.format(ISO_DATE), endDate.format(ISO_DATE));
        Collection<AbsenceDayEntity> absenceDaysToBeCleaned = this.absenceTargetService.readSchedules(beginDate, endDate).stream()
                .filter(a -> a.getName().equals(VACATION) || a.getName().equals(SICKNESS) || a.getName().equals(HOLIDAY))
                .collect(Collectors.toList());
        logger.info("{} absence days to be cleaned at target", absenceDaysToBeCleaned.size());
        absenceDaysToBeCleaned.stream()
                .map(AbsenceDayEntity::getId)
                .forEach(absenceTargetService::deleteSchedule);
        logger.info("cleaning all existing absence days from {} to {} at target ended",
                beginDate.format(ISO_DATE), endDate.format(ISO_DATE));
    }

    /**
     * TODO: add documentation
     * @param beginDate
     * @param endDate
     */
    public void importAbsenceDays(LocalDate beginDate, LocalDate endDate) {
        //cleanAbsenceDays(beginDate, endDate);
        logger.info("importing all absence days from {} to {} into target started", beginDate.format(ISO_DATE), endDate.format(ISO_DATE));
        List<String> personIds = personSourceService.getAllActivePersons().stream()
                .map(Person::getPersonId)
                .collect(Collectors.toList());

        logger.info("importing all holidays from {} to {} into target started", beginDate.format(ISO_DATE), endDate.format(ISO_DATE));
        Collection<AbsenceDayEntity> holidays = absenceSourceService.getHolidays(beginDate.getYear()).stream()
                .map(this::createAbsenceHoliday)
                .collect(Collectors.toList());

        personIds.stream()
                .filter(this::existPerson)
                .flatMap(id -> holidays.stream()
                        .map(d -> d.toBuilder()
                                .userId(parseInt(findPerson(id).getId()))
                                .build()))
                .forEach(absenceTargetService::createSchedule);
        logger.info("importing all holidays from {} to {} into target ended", beginDate.format(ISO_DATE), endDate.format(ISO_DATE));

        GetAbsencesRq payload = new GetAbsencesRq(
                Date.from(beginDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                Date.from(endDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                personIds,
                null,
                false
        );
        Map<String, List<AbsenceDayEntity>> absenceDaysByPersonnelNumber = absenceSourceService.getAbsencesInDays(payload);
        Map<Integer, List<AbsenceDayEntity>> absenceDaysByUserId = absenceDaysByPersonnelNumber.entrySet().stream()
                .filter(this::existPerson)
                .flatMap(this::buildAbsenceDayByPersonId)
                .collect(groupingBy(AbsenceDayEntity::getUserId));

        absenceDaysByUserId.values().stream().flatMap(Collection::stream)
                .filter(this::isWorkingDay)
                .filter(this::isSparta) // is not holiday
                .map(this::buildAbsenceDay)
                .forEach(absenceTargetService::createSchedule);
        logger.info("importing all absence days from {} to {} into target ended", beginDate.format(ISO_DATE), endDate.format(ISO_DATE));
    }

    private AbsenceDayEntity createAbsenceHoliday(Holiday h) {
        return AbsenceDayEntity.builder()
                .date(h.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .name(HOLIDAY)
                .am(!h.isHalfDay())
                .pm(true)
                .comment(h.getName() + " - automatically imported from HRworks")
                .overwrite(true)
                .build();
    }

    private boolean existPerson(Entry<String, List<AbsenceDayEntity>> entry) {
        return existPerson(entry.getKey());
    }

    private Stream<AbsenceDayEntity> buildAbsenceDayByPersonId(Entry<String, List<AbsenceDayEntity>> entry) {
        PersonEntity person = findPerson(entry.getKey());
        return entry.getValue().stream()
                .map(d -> addUserIdToAbsenceDay(parseInt(person.getId(), 10), d));
    }

    private AbsenceDayEntity addUserIdToAbsenceDay(int userId, AbsenceDayEntity day) {
        return day.toBuilder()
                .userId(userId)
                .build();
    }

    private boolean existPerson(String personId) {
        return personTargetService.getUsers().stream()
                .anyMatch(u -> u.getEmail().equals(personId));
    }

    private PersonEntity findPerson(String personId) {
        return personTargetService.getUsers().stream()
                .filter(u -> u.getEmail().equals(personId))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("could not find person in target by " + personId));
    }

    private AbsenceDayEntity buildAbsenceDay(AbsenceDayEntity entity) {
        return entity.toBuilder()
                .comment("automatically imported from HRworks")
                .am(isHalfHoliday(entity) != entity.isAm())
                .pm(isHalfHoliday(entity) != entity.isPm())
                .overwrite(true)
                .build();
    }

    private boolean isWorkingDay(AbsenceDayEntity entity) {
        final DayOfWeek dayOfWeek = entity.getDate().getDayOfWeek();
        return !(dayOfWeek.equals(SATURDAY) || dayOfWeek.equals(SUNDAY));
    }

    private boolean isHoliday(AbsenceDayEntity entity) {
        Collection<AbsenceDayEntity> days = absenceTargetService
                .readSchedules(entity.getDate(), entity.getDate(), ""+entity.getUserId());
        AbsenceDayEntity day = days.stream().findAny().orElse(null);
        return Objects.nonNull(day) && day.getName().equals(AbsenceTypeEnum.HOLIDAY) && !isHalfHoliday(day);
    }

    private boolean isSparta(AbsenceDayEntity entity) {
        return !isHoliday(entity);
    }

    private boolean isHalfHoliday(AbsenceDayEntity entity) {
        return entity.isAm() ^ entity.isPm();
    }
}
