package com.example.demo.api;

import com.example.demo.model.Person;
import com.example.demo.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("api/v1/person")
@RestController
public class PersonController {
    private final PersonService personservice;

    @Autowired
    public PersonController(PersonService personservice) {
        this.personservice = personservice;
    }
    @PostMapping
    public void addPerson(@RequestBody Person person){
        personservice.addPerson(person);
    }

    @GetMapping
    public List<Person> getAllPeople(){
        return personservice.getAllPeople();
    }

    @GetMapping(path ="{id}")
    public  Person getPersonById(@PathVariable("id") UUID id){
        return personservice.getPersonById(id)
                .orElse(null);

//        http://localhost:8080/api/v1/person/1234
    }

    @DeleteMapping(path ="{id}")
    public void deletePersonById(@PathVariable("id") UUID id){
        personservice.deletePerson(id);
    }

    @PutMapping(path ="{id}")
    public void updatePerson(@PathVariable("id") UUID id, @RequestBody Person personToUpdate){
        personservice.updatePerson(id, personToUpdate);
    }
}
