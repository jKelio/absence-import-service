package io.sparqs.absenceimport;

import io.sparqs.absenceimport.common.services.mail.ExceptionalMailContent;
import io.sparqs.absenceimport.common.services.mail.MailService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Component
@Getter
public class AbsenceImportTask {

    public static final String SENDER = "leon.jaekel@sparqs.io";
    public static final String[] RECEIVERS = { "leon.jaekel@sparqs.io", "info@leon-jaekel.com" };
    public static final String EXCEPTIONAL_MAIL_SUBJECT = "Import von Abwesenheiten fehlerhaft!";
    private final Logger logger = LoggerFactory.getLogger(AbsenceImportTask.class);
    private final ScheduledTaskHolder holder; // TODO: analysis and research how to interrupt already running and executed tasks with spring
    private final io.sparqs.absenceimport.AbsenceImportService absenceImportService;
    private final MailService mailService;

    private CompletableFuture<Void> future;
    private ExceptionalMailContent lastExceptionalContent;

    AbsenceImportTask(ScheduledTaskHolder holder, io.sparqs.absenceimport.AbsenceImportService absenceImportService, MailService mailService) {
        this.holder = holder;
        this.absenceImportService = absenceImportService;
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


    public boolean isDone() {
        return Objects.nonNull(future) && isDone();
    }

    public void join() {
        if (!isDone()) {
            logger.info("wait until previously started import is completed");
            future.join();
        }
    }

    private void cleanAndImportAbsenceDaysInternal() {
        final int currentYear = LocalDate.now().getYear();
        final LocalDate beginDate = LocalDate.parse(String.format("%s-%s-%s", currentYear, "01", "01"));
        final LocalDate endDate = LocalDate.parse(String.format("%s-%s-%s", currentYear, "12", "31"));
        try {
            absenceImportService.cleanAbsenceDays(beginDate, endDate);
            absenceImportService.importAbsenceDays(beginDate, endDate);
            mailService.sendTextualMail(
                    SENDER,
                   "Erfolgreicher Import aller Abwesenheiten",
                    "FYI",
                    RECEIVERS);
        } catch(Exception e) {
            lastExceptionalContent = ExceptionalMailContent.builder()
                    .customMessage(EXCEPTIONAL_MAIL_SUBJECT)
                    .exception(e)
                    .build();
            mailService.sendExceptionalMail(
                    SENDER,
                    EXCEPTIONAL_MAIL_SUBJECT,
                    lastExceptionalContent,
                    RECEIVERS
            );
        }
    }

}
