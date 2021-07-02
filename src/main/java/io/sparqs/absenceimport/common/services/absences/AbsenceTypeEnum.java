package io.sparqs.absenceimport.common.services.absences;

import java.util.Arrays;

import static java.util.Objects.nonNull;

public enum AbsenceTypeEnum {
    ABSENCE(null, "Abwesenheit", null, 5),
    HOLIDAY(null, "Feiertag", null, 2),
    UNPREDICTABLE(null, "Nicht planbar", null, 1),
    VACATION("Annual vacation", "Urlaub", "Checked, OK", 4),
    UNEXCUSED("Unexcused absence", null, "Checked, OK", 5),
    UNSALARIED("Unsalaried vacation", null, "Checked, OK", 5),
    TREATMENT("Treatment", null, "Checked, OK", 5),
    LONGTERM_PARENTAL("Longterm parental leave", null, "Checked, OK", 5),
    LONGTERM_MATERNITY("Longterm maternity leave", null, "Checked, OK", 5),
    OFFICIAL_ABSENCE("Official absence due to demission", null, "Checked, OK", 5),
    BIRTH("Birth", null, "Checked, OK", 5),
    WEDDING("Wedding", null, "Checked, OK", 5),
    NURSING_SICK_CHILDREN("Nursing of sick children", null, "Checked, OK", 5),
    MOVING("Umzug", null, "Checked, OK", 5),
    SICKNESS_CHILD("Sickness of child", null, "Checked, OK", 3),
    SICKNESS_WITHOUT_SICK_PAY("Sickness without sick pay", null, "Checked, OK", 3),
    SICKNESS_DURING_REINTEGRATION("Sickness during reintegration", null, "Checked, OK", 3),
    SICKNESS_WITHIN_28_DAYS("Sickness within the first 28 days of employment", null, "Checked, OK", 3),
    SICKNESS_WITHOUT_CERTIFICATE("Sickness without sickness certificate", null, "Checked, OK", 3),
    SICKNESS_WITH_CERTIFICATE("Sickness with sickness certificate", "Krankheit", "Reported", 3);

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
