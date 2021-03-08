package io.sparqs.hrworks;

import io.sparqs.hrworks.common.services.migrations.MigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Component
public class AbsenceImportTask {

    private final Logger logger = LoggerFactory.getLogger(AbsenceImportTask.class);
    private final ScheduledTaskHolder holder;
    private final MigrationService service;
    private CompletableFuture<Void>future;

    AbsenceImportTask(ScheduledTaskHolder holder, MigrationService service) {
        this.holder = holder;
        this.service = service;
    }

    @Scheduled(initialDelay = 10000, fixedDelay = 1500)
    public void importAbsences() {
        if (Objects.nonNull(future) && !future.isDone()) future.join();
        future = CompletableFuture.runAsync(() -> {
            int currentYear = LocalDate.now().getYear();
            LocalDate beginDate = LocalDate.parse(String.format("%s-%s-%s", currentYear, "01", "01"));
            LocalDate endDate = LocalDate.parse(String.format("%s-%s-%s", currentYear, "12", "31"));
            service.importAbsenceDays(beginDate, endDate);
        });
    }

    public void idle() {
        future = new CompletableFuture<>();
    }

    public CompletableFuture<Void> getFuture() {
        return future;
    }

}
