package io.sparqs.hrworks.common.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static io.sparqs.hrworks.common.jackson.LocalDateWrapper.EXPECTED_SERIALIZATION;

class LocalDateDeserializerTest {

    @Test
    public void testLocalDateDeserialization() throws JsonProcessingException {
        new ObjectMapper()
                .readValue(EXPECTED_SERIALIZATION, LocalDateWrapper.class)
                .assertDate();
    }

}