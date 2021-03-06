package io.sparqs.absenceimport.common.services.absences;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.sparqs.absenceimport.common.mapping.LocalDateDeserializer;
import io.sparqs.absenceimport.common.mapping.LocalDateSerializer;
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
    private String personId;
    private String comment;

    @JsonProperty("user_id")
    private int userId;
    private String userFirstName;
    private String userLastName;
    private boolean overwrite;

    @JsonProperty("assignment")
    private void getAssignment(Map<String, String> map) {
        try {
            name = AbsenceTypeEnum.fromTarget(map.get("name"));
            type = map.get("type");
        } catch (Exception e) {
            name = null;
            type = null;
        }
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
