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

    @Scheduled(initialDelay = 0, cron = "0 8 * * *")
    public void importAbsences() {
        join();
        future = CompletableFuture.runAsync(() -> {
            int currentYear = LocalDate.now().getYear();
            LocalDate beginDate = LocalDate.parse(String.format("%s-%s-%s", currentYear, "01", "01"));
            LocalDate endDate = LocalDate.parse(String.format("%s-%s-%s", currentYear, "12", "31"));
            service.importAbsenceDays(beginDate, endDate);
        });
    }

    public void interrupt() {
        logger.info("interrupt running absence import task manually");
        join();
        future = new CompletableFuture<>();
    }

    public void cancel() {
        future.cancel(true);
    }

    public boolean isDone() {
        return future.isDone();
    }

    private void join() {
        if (Objects.nonNull(future) && !isDone()) {
            logger.info("wait until previously started import is completed");
            future.join();
        }
    }

}
