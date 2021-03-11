package io.sparqs.absenceimport.api.absences;

import io.sparqs.absenceimport.AbsenceImportService;
import io.sparqs.absenceimport.AbsenceImportTask;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.LOCKED;

@RestController
public class AbsencesController {

    private final AbsenceImportTask task;
    private final AbsenceImportService service;

    AbsencesController(AbsenceImportTask task, AbsenceImportService service) {
        this.task = task;
        this.service = service;
    }

    @PostMapping("api/absences")
    public void cleanAndImportAbsences() {
        if (task.isBusy()) {
            throw new ResponseStatusException(LOCKED, "blocked due to ongoing clean-up and import process");
        }
        task.cleanAndImportAbsences();
    }

    @GetMapping("api/absences/lastimport")
    public ResponseEntity<AbsencesLastImportResource> getLastImportOfAbsences() {
        return ResponseEntity.ok().body(AbsencesLastImportResource.builder()
                .status(service.getStatus())
                .lastCleanedDateTime(service.getLastCleanedDateTime())
                .lastImportedDateTime(service.getLastImportedDateTime())
                .lastExceptionalContent(task.getLastExceptionalContent())
                .build());
    }

}
