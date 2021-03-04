package io.sparqs.hrworks.common.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocalDateSerializerTest {

    @Test
    public void testLocalDateSerialization() throws JsonProcessingException {
        assertEquals(LocalDateWrapper.EXPECTED_SERIALIZATION, new ObjectMapper()
                .writeValueAsString(LocalDateWrapper.builder().date(LocalDateWrapper.EXPECTED_LOCAL_DATE).build()));
    }

}