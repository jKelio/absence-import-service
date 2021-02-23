package io.sparqs.hrworks.common.services.persons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PersonEntity {

    private String id;

    @JsonProperty("firstname")
    private String firstName;

    @JsonProperty("lastname")
    private String lastName;
    private boolean active;
    private boolean extern;

    private String email;

    @JsonProperty("mobile_phone")
    private String mobilePhone;

    @JsonProperty("work_phone")
    private String workPhone;

    @JsonProperty("home_address")
    private String homeAddress;

    @JsonProperty("info")
    private String info;
    private String birthday;

    @JsonProperty("avatar_url")
    private String avatarUrl;
    private String unitName;

    @JsonProperty("unit")
    private void getUnitName(Map<String, String> unit) {
        unitName = unit.get("name");
    }
}
