package io.sparqs.hrworks.common.services.persons;

import com.aoe.hrworks.HrWorksClient;
import com.aoe.hrworks.Person;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.internal.LinkedTreeMap;
import io.reactivex.Single;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PersonSourceServiceTest {

    public static final String PERSONNEL_NUMBER = "1002";

    private PersonSourceService service;
    private HrWorksClient client;

    @BeforeEach
    void setUp() {
        client = mock(HrWorksClient.class);
        service = new PersonSourceService(client);
    }

    @AfterEach
    void tearDown() {
        client = null;
        service = null;
    }

    @Test
    public void testGetAllActivePersons() throws IOException {
        mockGetAllActivePersonsApi();
        assertEquals(loadExpectedPersons().size(), service.getAllActivePersons().size());
    }

    @Test
    public void testGetActivePersonByPersonnelNumber() throws IOException {
        mockGetAllActivePersonsApi();
        assertEquals(PERSONNEL_NUMBER, service.getActivePerson(PERSONNEL_NUMBER).getPersonnelNumber());
    }

    private void mockGetAllActivePersonsApi() throws IOException {
        when(client.getAllActivePersons())
                .thenReturn(Single.just(loadMockedPersons()));
    }

    public static Map<String, List<Person>> loadMockedPersons() throws IOException {
        InputStream is = PersonSourceServiceTest.class.getClassLoader()
                .getResourceAsStream("mocks/source/persons.json");
        Map<String, List<TransferPerson>> transferPersons = new ObjectMapper().readValue(is, new TypeReference<>() {});
        Map<String, List<Person>> persons = new LinkedTreeMap<>();
        transferPersons.keySet()
                .forEach(u -> persons.put(u, transferPersons.get(u).stream()
                        .map(TransferPerson::getPerson)
                        .collect(Collectors.toList())));
        return persons;
    }

    public static List<Person> loadExpectedPersons() throws IOException {
        InputStream is = PersonSourceServiceTest.class.getClassLoader()
                .getResourceAsStream("mocks/source/expected-persons.json");
        List<TransferPerson> transferPersons = new ObjectMapper().readValue(is, new TypeReference<>() {});
        return transferPersons.stream()
                .map(TransferPerson::getPerson)
                .collect(Collectors.toList());
    }


    @Getter
    private static class TransferPerson {

        private final Person person;

        @JsonCreator
        TransferPerson(
                @JsonProperty("personnelNumber") String personnelNumber,
                @JsonProperty("personId") String personId,
                @JsonProperty("firstName") String firstName,
                @JsonProperty("lastName") String lastName
        ) {
            person = new Person(personnelNumber, personId, firstName, lastName);
        }
    }

}