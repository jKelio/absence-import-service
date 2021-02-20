package io.sparqs.hrworks.common.services.absences;

public enum AbsenceTypeEnum {
    VACATION("Annual vacation", "Urlaub"),
    SICKNESS("Sickness with sickness certificate", "Krankheit"),
    HOLIDAY(null, "Feiertag");

    private final String hrWorks;
    private final String moco;

    AbsenceTypeEnum(String hrWorks, String moco) {
        this.hrWorks = hrWorks;
        this.moco = moco;
    }

    public String getHrWorks() {
        return hrWorks;
    }

    public String getMoco() {
        return moco;
    }
}
