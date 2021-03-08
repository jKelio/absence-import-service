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
    private final ScheduledTaskHolder holder; // TODO: analysis and research how to interrupt already running and executed tasks with spring
    private final MigrationService service;
    private CompletableFuture<Void> future;

    AbsenceImportTask(ScheduledTaskHolder holder, MigrationService service) {
        this.holder = holder;
        this.service = service;
    }

    @Scheduled(cron = "0 8 * * * *")
    @Scheduled(cron = "0 16 * * * *")
    public void cleanAndImportAbsences() {
        join();
        future = CompletableFuture
                .runAsync(this::cleanAndImportAbsenceDaysInternal);
    }

    public void interrupt() {
        logger.info("interrupt running absence import task manually");
        join();
        future = new CompletableFuture<>();
    }

    public void complete() {
        future.complete(null);
    }

    public void completeExceptionally(Throwable throwable) {
        future.completeExceptionally(throwable);
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

    private void cleanAndImportAbsenceDaysInternal() {
        final int currentYear = LocalDate.now().getYear();
        final LocalDate beginDate = LocalDate.parse(String.format("%s-%s-%s", currentYear, "01", "01"));
        final LocalDate endDate = LocalDate.parse(String.format("%s-%s-%s", currentYear, "12", "31"));
        service.cleanAbsenceDays(beginDate, endDate);
        service.importAbsenceDays(beginDate, endDate);
    }

}
