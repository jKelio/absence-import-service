package io.sparqs.hrworks.api.persons;

import com.aoe.hrworks.Person;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static io.sparqs.hrworks.HrWorksApplication.API_PATH_PREFIX;

@RestController
public class PersonsController {

    public static final String PERSONS = API_PATH_PREFIX + "persons";
    private final PersonService service;

    public PersonsController(PersonService service) {
        this.service = service;
    }

    @GetMapping(PERSONS)
    public ResponseEntity<List<Person>> getAllActivePersons() {
        return ResponseEntity.ok().body(service.getAllActivePersons());
    }

    @GetMapping(PERSONS + "/{personnelNumber}")
    public ResponseEntity<Person> getActivePerson(@PathVariable("personnelNumber") String personnelNumber) {
        return ResponseEntity.ok().body(service.getActivePerson(personnelNumber));
    }
}
