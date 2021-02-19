package io.sparqs.hrworks.api.absences;

import com.aoe.hrworks.AbsenceData;
import com.aoe.hrworks.AbsenceTypeList;
import com.aoe.hrworks.GetAbsencesRq;
import io.sparqs.hrworks.HrWorksApplication;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static io.sparqs.hrworks.HrWorksApplication.API_PATH_PREFIX;

@RestController
public class AbsencesController {

    public static final String ABSENCES = API_PATH_PREFIX + "/absences";
    private final AbsenceService service;

    public AbsencesController(AbsenceService service) {
        this.service = service;
    }

    @GetMapping(ABSENCES)
    public ResponseEntity<Map<String, List<AbsenceData>>> getAbsences(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date beginDate,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam("personnelNumber") List<String> idOrPersonnelNumberList
    ) {
        return ResponseEntity.ok().body(service.getAbsences(new GetAbsencesRq(
                        beginDate,
                        endDate,
                        idOrPersonnelNumberList,
                        null,
                        true)));
    }

    @GetMapping(ABSENCES + "/types")
    public ResponseEntity<AbsenceTypeList> getAbsenceTypes() {
        return ResponseEntity.ok().body(service.getAllAbsenceTypes());
    }

}
