package az.ingress.hrms.service.impl.person;

import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.entity.person.PersonPhoto;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.mapper.PersonMapper;
import az.ingress.hrms.repository.PersonRepository;
import az.ingress.hrms.service.person.PersonService;
import az.ingress.hrms.dto.person.PersonRequest;
import az.ingress.hrms.dto.person.PersonResponse;
import az.ingress.hrms.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PersonServiceImpl implements PersonService {
    private final PersonRepository repository;
    private final PersonMapper mapper;

    @Override
    @Transactional
    public PersonResponse create(PersonRequest request) {

        Person person = mapper.toEntity(request);

        repository.save(person);

        return mapper.toResponse(person);

    }

    @Override
    @Transactional
    public PersonResponse update(Integer id, PersonRequest request) {

        Person person = fetchPerson(id);

        mapper.updateEntity(person, request);

        repository.save(person);

        return mapper.toResponse(person);
    }

    @Override
    public PersonResponse getById(Integer id) {
        return mapper.toResponse(fetchPerson(id));    }

    @Override
    public List<PersonResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();    }

    @Override
    @Transactional
    public void softDelete(Integer id) {

        Person person = fetchPerson(id);

        person.setIsDeleted(true);
        person.setDeletedAt(LocalDateTime.now());
        person.setDeletedBy("SYSTEM");

        repository.save(person);

    }

    @Override
    @Transactional
    public void restore(Integer id) {

        Person person = repository.findByIdWithDeleted(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Person not found."));

        if (!Boolean.TRUE.equals(person.getIsDeleted())) {
            throw new IllegalStateException("Resource is not deleted.");
        }

        person.setIsDeleted(false);
        person.setDeletedAt(null);
        person.setDeletedBy(null);

        repository.save(person);
    }

    private Person fetchPerson(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(s -> {
                                throw new DeletedResourceException(
                                        "Person is deleted."
                                );
                            });
                    throw new ResourceNotFoundException(
                            "Person not found.");
                });
    }
}
