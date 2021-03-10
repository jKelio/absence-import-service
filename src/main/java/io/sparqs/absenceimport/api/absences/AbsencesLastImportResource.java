package io.sparqs.absenceimport.api.absences;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.sparqs.absenceimport.AbsenceImportStatus;
import io.sparqs.absenceimport.common.services.mail.ExceptionalMailContent;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

@Builder
@Getter
@JsonInclude(NON_NULL)
public class AbsencesLastImportResource {
    private final AbsenceImportStatus status;
    private final LocalDateTime lastCleanedDateTime;
    private final LocalDateTime lastImportedDateTime;
    private final ExceptionalMailContent lastExceptionalContent;
}
