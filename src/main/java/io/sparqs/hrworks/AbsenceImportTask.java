package io.sparqs.hrworks;

import com.aoe.hrworks.Absence;
import io.sparqs.hrworks.common.services.migrations.MigrationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AbsenceImportTask {

    private final ScheduledTaskHolder holder;
    private final MigrationService service;

    AbsenceImportTask(ScheduledTaskHolder holder, MigrationService service) {
        this.holder = holder;
        this.service = service;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 5000)
    public void importAbsences() {
        int currentYear = LocalDate.now().getYear();
        LocalDate beginDate = LocalDate.parse(String.format("%s-%s-%s", currentYear, "01", "01"));
        LocalDate endDate = LocalDate.parse(String.format("%s-%s-%s", currentYear, "12", "31"));
        service.importAbsenceDays(beginDate, endDate);
    }
}
