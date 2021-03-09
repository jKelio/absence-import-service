package io.sparqs.hrworks;

import io.sparqs.hrworks.common.services.mail.ExceptionalMailContent;
import io.sparqs.hrworks.common.services.mail.MailService;
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

    public static final String SENDER = "leon.jaekel@sparqs.io";
    public static final String[] RECEIVERS = { "leon.jaekel@sparqs.io", "info@leon-jaekel.com" };
    private final Logger logger = LoggerFactory.getLogger(AbsenceImportTask.class);
    private final ScheduledTaskHolder holder; // TODO: analysis and research how to interrupt already running and executed tasks with spring
    private final AbsenceImportService service;
    private final MailService mailService;
    private CompletableFuture<Void> future;

    AbsenceImportTask(ScheduledTaskHolder holder, AbsenceImportService service, MailService mailService) {
        this.holder = holder;
        this.service = service;
        this.mailService = mailService;
    }

    @Scheduled(cron = "0 0 8-16 * * *")
    public void cleanAndImportAbsences() {
        join();
        future = CompletableFuture.runAsync(this::cleanAndImportAbsenceDaysInternal);
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

    public void join() {
        if (Objects.nonNull(future) && !isDone()) {
            logger.info("wait until previously started import is completed");
            future.join();
        }
    }

    private void cleanAndImportAbsenceDaysInternal() {
        final int currentYear = LocalDate.now().getYear();
        final LocalDate beginDate = LocalDate.parse(String.format("%s-%s-%s", currentYear, "01", "01"));
        final LocalDate endDate = LocalDate.parse(String.format("%s-%s-%s", currentYear, "12", "31"));
        try {
            service.cleanAbsenceDays(beginDate, endDate);
            service.importAbsenceDays(beginDate, endDate);
            mailService.sendTextualMail(
                    SENDER,
                   "Erfolgreicher Import aller Abwesenheiten",
                    "FYI",
                    RECEIVERS);
        } catch(Exception e) {
            mailService.sendExceptionalMail(
                    SENDER,
                    "Fehler beim Import aller Abwesenheiten",
                    ExceptionalMailContent.builder().exception(e).build(),
                    RECEIVERS
            );
        }
    }

}
