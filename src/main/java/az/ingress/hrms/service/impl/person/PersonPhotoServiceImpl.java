package az.ingress.hrms.service.impl.person;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.PersonPhotoSearchCriteria;
import az.ingress.hrms.dto.personPhoto.PersonPhotoCreateRequest;
import az.ingress.hrms.dto.personPhoto.PersonPhotoResponse;
import az.ingress.hrms.dto.personPhoto.PersonPhotoUpdateRequest;
import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.entity.person.PersonPhoto;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.DuplicateResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.helper.StatusHelper;
import az.ingress.hrms.log.LogAction;
import az.ingress.hrms.log.person.personPhoto.PersonPhotoLogService;
import az.ingress.hrms.mapper.person.PersonPhotoMapper;
import az.ingress.hrms.repository.person.PersonPhotoRepository;
import az.ingress.hrms.repository.person.PersonRepository;
import az.ingress.hrms.service.person.PersonPhotoService;
import az.ingress.hrms.specification.person.PersonPhotoSpecification;
import az.ingress.hrms.util.PaginationUtils;
import az.ingress.hrms.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonPhotoServiceImpl implements PersonPhotoService {

    private final PersonRepository personRepository;
    private final PersonPhotoRepository repository;
    private final PersonPhotoMapper mapper;
    private final StatusHelper statusHelper;
    private final PersonPhotoLogService personPhotoLogService;

    @Override
    @Transactional
    public PersonPhotoResponse create(PersonPhotoCreateRequest request) {
        Person person = personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Person not found with id: " + request.getPersonId()
                ));

        String filePath = request.getFilePath().trim();

        if (repository.existsByPersonAndFilePath(person, filePath)) {
            throw new DuplicateResourceException("Person with this photo already exists.");
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
        entity.setStatus(statusHelper.getActive());

        repository.save(entity);

        personPhotoLogService.log(
                entity,
                LogAction.POST,
                SecurityUtils.getCurrentUsername()
        );

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

        personPhotoLogService.log(
                entity,
                LogAction.PUT,
                SecurityUtils.getCurrentUsername()
        );

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
    public PageResponse<PersonPhotoResponse> getAll(PersonPhotoSearchCriteria criteria, Pageable pageable) {
        Specification<PersonPhoto> specification = PersonPhotoSpecification.build(criteria);
        Page<PersonPhoto> page = repository.findAll(specification, pageable);

        return PaginationUtils.toPageResponse(page, mapper::toResponse);
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
    @Transactional
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

        personPhotoLogService.log(
                entity,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()
        );

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

        personPhotoLogService.log(
                entity,
                LogAction.DELETE,
                SecurityUtils.getCurrentUsername()
        );

        entity.setIsDeleted(true);
        entity.setDeletedBy(SecurityUtils.getCurrentUsername());
        entity.setDeletedAt(LocalDateTime.now());

        repository.save(entity);
    }

    @Override
    @Transactional
    public void restore(Integer id) {
        PersonPhoto entity = repository.findByIdWithDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person Photo not found."));

        if (!Boolean.TRUE.equals(entity.getIsDeleted())) {
            throw new IllegalStateException("Person Photo is not deleted.");
        }

        personPhotoLogService.log(
                entity,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()
        );

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

        personPhotoLogService.log(
                entity,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()
        );

        entity.setStatus(statusHelper.getActive());

        repository.save(entity);

        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public PersonPhotoResponse deactivate(Integer id) {
        PersonPhoto entity = fetchPersonPhoto(id);

        personPhotoLogService.log(
                entity,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()
        );

        entity.setStatus(statusHelper.getInactive());

        repository.save(entity);

        return mapper.toResponse(entity);
    }

    private PersonPhoto fetchPersonPhoto(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(s -> {
                                throw new DeletedResourceException("Photo is deleted.");
                            });
                    throw new ResourceNotFoundException("Person Photo not found.");
                });
    }
}