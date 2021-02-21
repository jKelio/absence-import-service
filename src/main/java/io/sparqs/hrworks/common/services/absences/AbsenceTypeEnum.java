package io.sparqs.hrworks.common.services.absences;

import java.util.Arrays;

import static java.util.Objects.nonNull;

public enum AbsenceTypeEnum {
    VACATION("Annual vacation", "Urlaub", "Checked, OK", 4),
    SICKNESS("Sickness with sickness certificate", "Krankheit", "Reported", 3),
    HOLIDAY(null, "Feiertag", null, 2);

    private final String source;
    private final String target;
    private final String status;
    private final int code;

    AbsenceTypeEnum(String source, String target, String status, int code) {
        this.source = source;
        this.target = target;
        this.status = status;
        this.code = code;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public String getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }

    public static AbsenceTypeEnum fromSource(String absenceType) {
        return Arrays.stream(values())
                .filter(t -> nonNull(t.getSource()))
                .filter(t -> t.getSource().equals(absenceType))
                .findAny()
                .orElseThrow();
    }

    public static AbsenceTypeEnum fromTarget(String absenceType) {
        return Arrays.stream(values())
                .filter(t -> nonNull(t.getTarget()))
                .filter(t -> t.getTarget().equals(absenceType))
                .findAny()
                .orElseThrow();
    }
}
