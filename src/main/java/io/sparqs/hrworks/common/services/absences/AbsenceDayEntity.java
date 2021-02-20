package io.sparqs.hrworks.common.services.absences;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class AbsenceDayEntity {
    private final Long id;
    private final LocalDate date;
    private final boolean am;
    private final boolean pm;
    private final String name;
    private final String type;

    @JsonIgnore
    private final String personnelNumber;
}
