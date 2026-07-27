package az.ingress.hrms.service.impl.person;

import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.mapper.PersonMapper;
import az.ingress.hrms.repository.PersonRepository;
import az.ingress.hrms.service.person.PersonService;
import az.ingress.hrms.dto.person.PersonRequest;
import az.ingress.hrms.dto.person.PersonResponse;
import az.ingress.hrms.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PersonServiceImpl implements PersonService {
    private final PersonRepository repository;
    private final PersonMapper mapper;

    @Override
    public PersonResponse create(PersonRequest request) {

        Person person = mapper.toEntity(request);

        repository.save(person);

        return mapper.toResponse(person);

    }

    @Override
    public PersonResponse update(Integer id, PersonRequest request) {

        Person person = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Person with id " + id + " was not found."
                ));

        mapper.updateEntity(person, request);

        repository.save(person);

        return mapper.toResponse(person);
    }

    @Override
    public PersonResponse getById(Integer id) {
       Person person = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Person with id " + id + " was not found."
                        ));
        return mapper.toResponse(person);    }

    @Override
    public List<PersonResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();    }

    @Override
    public void softDelete(Integer id) {

        Person person = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Person not found."
                        ));

        person.setIsDeleted(true);
        person.setDeletedAt(LocalDateTime.now());
        person.setDeletedBy("SYSTEM");

        repository.save(person);

    }

    @Override
    public void restore(Integer id) {

        Person person = repository.findByIdWithDeleted(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Person not found."));

        person.setIsDeleted(false);
        person.setDeletedAt(null);
        person.setDeletedBy(null);

        repository.save(person);
    }
}
