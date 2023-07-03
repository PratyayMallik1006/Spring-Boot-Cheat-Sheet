# Spring Boot Cheat Sheet
## Setting Up
1. Download Boilerplate code : https://start.spring.io/
- In Dependencies add Spring Web
- In Project choose Maven
2. Open with IntelliJ

## RESTful API
1. Make Model Package
- **Person.java (class)**
```java
public class Person {  
	private final UUID id;  
	private final String name;  
	  
	public Person(@JsonProperty("id") UUID id, @JsonProperty("name") String name) {  
	this.id = id;  
	this.name = name;  
	}  
	  
	public UUID getId() {  
	return id;  
	}  
	  
	public String getName() {  
	return name;  
	}  
}
```
2. Make DAO Package (Later to be replaced by DBMS)
- **PersonDao.java (Interface)**
```java
public interface PersonDao {  
	int insertPerson(UUID id, Person person);  
	default int insertPerson(Person person){  
		UUID id = UUID.randomUUID();  
		return insertPerson(id, person);  
	}  
	  
	List<Person> selectAllPeople();  
	  
	Optional<Person> selectPersonById(UUID id);  
	int deletePersonById(UUID id);  
	int updatePersonById(UUID id, Person person);  
}
```
- **FakePersonDataAccessService.java (class)**
```java
@Repository("fakeDao")  
public class FakePersonDataAccessService implements PersonDao{  
private static List<Person> DB = new ArrayList<>();  
@Override  
public int insertPerson(UUID id, Person person) {  
	DB.add(new Person(id, person.getName()));  
	return 1;  
}  
  
@Override  
public List<Person> selectAllPeople() {  
	return DB;  
}  
  
@Override  
public Optional<Person> selectPersonById(UUID id) {  
	return DB.stream()  
	.filter(person -> person.getId().equals(id))  
	.findFirst();  
}  
  
@Override  
public int deletePersonById(UUID id) {  
Optional<Person> personMaybe = selectPersonById(id);  
if(personMaybe.isEmpty()){  
return 0;  
}  
DB.remove(personMaybe.get());  
return 1;  
}  
  
@Override  
public int updatePersonById(UUID id, Person update) {  
return selectPersonById(id)  
.map(person -> {  
int indexOfPersonToUpdate = DB.indexOf(person);  
if(indexOfPersonToUpdate >= 0){  
DB.set(indexOfPersonToUpdate, new Person(id, update.getName()));  
return 1;  
}  
return 0;  
})  
.orElse(0);  
}
}
```

3. Make Service Package
- **PersonService.java (class)**
```java
@Service  
public class PersonService {  
private final PersonDao personDao;  
  
@Autowired  
public PersonService(@Qualifier("fakeDao") PersonDao personDao){  
	this.personDao = personDao;  
}  
// @Qualifier makes it easy to switch Data Access Service
  
  
public int addPerson(Person person){  
	return personDao.insertPerson(person);  
}  
  
public List<Person> getAllPeople(){  
	return personDao.selectAllPeople();  
}  
public Optional<Person> getPersonById(UUID id){  
return personDao.selectPersonById(id);  
}  
public int deletePerson(UUID id){  
return personDao.deletePersonById(id);  
}  
public int updatePerson(UUID id , Person newPerson){  
return personDao.updatePersonById(id, newPerson);  
}
}
```
4. Make API Package
- **PersonController.java (class)**
```java

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
//	localhost:8080/api/v1/person/
}  
  
@GetMapping(path ="{id}")  
public Person getPersonById(@PathVariable("id") UUID id){  
return personservice.getPersonById(id)  
.orElse(null);  
  
// localhost:8080/api/v1/person/bd53f9a8-bae1-4e1c-a0d1-8dceb8958fb6
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
```
 
