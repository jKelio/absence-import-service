package io.sparqs.hrworks.common.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Month;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
class LocalDateWrapper {

    @JsonIgnore
    private static String EXPECTED_DATE_STRING = "2021-03-04";

    @JsonIgnore
    static LocalDate EXPECTED_LOCAL_DATE = LocalDate.parse(EXPECTED_DATE_STRING);

    @JsonIgnore
    public static String EXPECTED_SERIALIZATION = "{\"date\":\"" + EXPECTED_DATE_STRING + "\"}";

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;

    public void assertDate() {
        assertNotNull(getDate());
        assertEquals(getDate().getYear(), 2021);
        assertEquals(getDate().getMonth(), Month.MARCH);
        assertEquals(getDate().getDayOfMonth(), 4);
    }
}
