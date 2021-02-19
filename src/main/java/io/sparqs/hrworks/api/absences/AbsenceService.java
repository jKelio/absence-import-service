package io.sparqs.hrworks.api.absences;

import com.aoe.hrworks.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AbsenceService {

    private final HrWorksClient client;

    AbsenceService(HrWorksClient client) {
        this.client = client;
    }
    
    public AbsenceTypeList getAllAbsenceTypes() {
        return client.getAllAbsenceTypes().blockingGet();
    }

    public Map<String, List<AbsenceData>> getAbsences(GetAbsencesRq payload) {
        return client.getAbsences(payload).blockingGet();
    }
}
