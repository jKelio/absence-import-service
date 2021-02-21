package io.sparqs.hrworks.common.services.absences;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.sparqs.hrworks.common.jackson.LocalDateDeserializer;
import io.sparqs.hrworks.common.jackson.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbsenceDayEntity {
    private Long id;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;

    private boolean am;
    private boolean pm;
    private AbsenceTypeEnum name;
    private String type;
    private String personnelNumber;
    private String comment;

    @JsonProperty("user_id")
    private int userId;
    private String userFirstName;
    private String userLastName;

    @JsonProperty("assignment")
    private void getAssignment(Map<String, String> map) {
        name = AbsenceTypeEnum.fromTarget(map.get("name"));
        type = map.get("type");
    }

    @JsonProperty("user")
    private void getUser(Map<String, Object> map) {
        userId = (int) map.get("id");
        userFirstName = (String) map.get("firstname");
        userLastName = (String) map.get("lastname");
    }

    @JsonIgnoreProperties
    @JsonProperty("absence_code")
    public int getAbsenceCode() {
        return name.getCode();
    }
}
