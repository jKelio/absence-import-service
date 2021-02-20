package io.sparqs.hrworks.common.services.persons;

import com.aoe.hrworks.HrWorksClient;
import com.aoe.hrworks.Person;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PersonSourceService {

    private final HrWorksClient client;

    PersonSourceService(HrWorksClient client) {
        this.client = client;
    }

    public List<Person> getAllActivePersons() {
        Map<String, List<Person>> persons = client.getAllActivePersons().blockingGet();
        return persons.keySet().stream()
                .map(persons::get)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public Person getActivePerson(String personnelNumber) {
        return getAllActivePersons().stream()
                .filter(p -> p.getPersonnelNumber().equals(personnelNumber))
                .findAny()
                .orElseThrow();
    }
}
