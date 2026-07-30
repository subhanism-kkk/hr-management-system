package az.ingress.hrms.service.impl.person;

import az.ingress.hrms.dto.personPhoto.PersonPhotoCreateRequest;
import az.ingress.hrms.dto.personPhoto.PersonPhotoResponse;
import az.ingress.hrms.dto.personPhoto.PersonPhotoUpdateRequest;
import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.entity.person.PersonPhoto;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.DuplicateResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.helper.StatusHelper;
import az.ingress.hrms.mapper.PersonPhotoMapper;
import az.ingress.hrms.repository.PersonPhotoRepository;
import az.ingress.hrms.repository.PersonRepository;
import az.ingress.hrms.service.person.PersonPhotoService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PersonPhotoServiceImpl implements PersonPhotoService {

    private final PersonRepository personRepository;
    private final PersonPhotoRepository repository;
    private final PersonPhotoMapper mapper;
    private final StatusHelper statusHelper;


    @Override
    @Transactional
    public PersonPhotoResponse create(PersonPhotoCreateRequest request) {
        Person person = personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Person not found with id: " + request.getPersonId()
                ));

        String filePath = request.getFilePath().trim();

        if (repository.existsByPersonAndFilePath(person, filePath)) {
            throw new DuplicateResourceException(
                    "Person with this photo already exists.");
        }

        if (Boolean.TRUE.equals(request.getIsMain())) {

            repository.findByPersonAndIsMainTrue(person)
                    .ifPresent(oldMainPhoto -> {
                        oldMainPhoto.setIsMain(false);
                        repository.save(oldMainPhoto);
                    });
        }

        PersonPhoto entity = mapper.toEntity(request);

        entity.setPerson(person);
        entity.setFilePath(filePath);

        repository.save(entity);

        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public PersonPhotoResponse update(Integer id, PersonPhotoUpdateRequest request) {
        PersonPhoto entity = fetchPersonPhoto(id);

        if (Boolean.TRUE.equals(request.getIsMain())) {

            repository.findByPersonAndIsMainTrue(entity.getPerson())
                    .ifPresent(oldMainPhoto -> {
                        if (!oldMainPhoto.getId().equals(entity.getId())) {
                            oldMainPhoto.setIsMain(false);
                            repository.save(oldMainPhoto);
                        }
                    });
        }

        String filePath = request.getFilePath().trim();

        if (!entity.getFilePath().equalsIgnoreCase(filePath)
                && repository.existsByPersonAndFilePath(entity.getPerson(), filePath)) {
            throw new DuplicateResourceException("Another photo with this file path already exists.");
        }

        mapper.updateEntity(entity, request);
        entity.setFilePath(filePath);

        repository.save(entity);

        return mapper.toResponse(entity);
    }


    @Override
    public PersonPhotoResponse getById(Integer id) {
        return mapper.toResponse(fetchPersonPhoto(id));
    }

    @Override
    public List<PersonPhotoResponse> getAll() {
        return repository
                .findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<PersonPhotoResponse> getAllByPerson(Integer personId) {

        if (!personRepository.existsById(personId)) {
            throw new ResourceNotFoundException("Person not found with id: " + personId);
        }

        return repository.findByPersonId(personId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PersonPhotoResponse getMainPhoto(Integer personId) {

        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Person not found with id: " + personId
                ));

        PersonPhoto entity = repository.findByPersonAndIsMainTrue(person)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Main photo not found for person with id: " + personId
                ));

        return mapper.toResponse(entity);
    }

    @Override
    public void setMainPhoto(Integer photoId) {

        PersonPhoto entity = fetchPersonPhoto(photoId);

        if (Boolean.TRUE.equals(entity.getIsMain())) {
            return;
        }
        Person person = entity.getPerson();

        repository.findByPersonAndIsMainTrue(person)
                .ifPresent(oldMainPhoto -> {
                    if (!oldMainPhoto.getId().equals(entity.getId())) {
                        oldMainPhoto.setIsMain(false);
                        repository.save(oldMainPhoto);
                    }
                });

        entity.setIsMain(true);
        repository.save(entity);

    }

    @Override
    @Transactional
    public void softDelete(Integer id) {

        PersonPhoto entity = fetchPersonPhoto(id);

        if (Boolean.TRUE.equals(entity.getIsMain())) {
            entity.setIsMain(false);
        }

        entity.setIsDeleted(true);
        entity.setDeletedBy("SYSTEM");
        entity.setDeletedAt(LocalDateTime.now());

        repository.save(entity);

    }

    @Override
    public void restore(Integer id) {

        PersonPhoto entity = repository.findByIdWithDeleted(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Person Photo not found."
                        ));

        if (!entity.getIsDeleted()) {
            throw new IllegalStateException(
                    "Person Photo is not deleted."
            );
        }

        entity.setIsMain(false);
        entity.setIsDeleted(false);
        entity.setDeletedAt(null);
        entity.setDeletedBy(null);

        repository.save(entity);

    }

    @Override
    @Transactional
    public PersonPhotoResponse activate(Integer id) {

        PersonPhoto entity = fetchPersonPhoto(id);

        entity.setStatus(statusHelper.getActive());

        repository.save(entity);

        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public PersonPhotoResponse deactivate(Integer id) {

        PersonPhoto entity = fetchPersonPhoto(id);

        entity.setStatus(statusHelper.getInactive());

        repository.save(entity);

        return mapper.toResponse(entity);
    }


    private PersonPhoto fetchPersonPhoto(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(s -> {
                                throw new DeletedResourceException(
                                        "Photo is deleted."
                                );
                            });
                    throw new ResourceNotFoundException(
                            "Person Photo not found.");
                });
    }

}
