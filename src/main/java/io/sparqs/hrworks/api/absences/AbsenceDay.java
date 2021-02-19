package io.sparqs.hrworks.api.absences;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class AbsenceDay {
    private final Long id;
    private final LocalDate date;
    private final Boolean am;
    private final Boolean pm;
    private final String name;
    private final String type;
    private final String personnelNumber;
}
