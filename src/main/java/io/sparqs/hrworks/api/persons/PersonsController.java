package io.sparqs.hrworks.api.persons;

import com.aoe.hrworks.Person;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PersonsController {

    public static final String PERSONS = "persons";
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
