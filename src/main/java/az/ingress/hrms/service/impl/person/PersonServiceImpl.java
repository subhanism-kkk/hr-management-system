package az.ingress.hrms.service.impl.person;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.PersonSearchCriteria;
import az.ingress.hrms.dto.person.PersonRequest;
import az.ingress.hrms.dto.person.PersonResponse;
import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.helper.StatusHelper;
import az.ingress.hrms.log.LogAction;
import az.ingress.hrms.log.person.person.PersonLogService;
import az.ingress.hrms.mapper.PersonMapper;
import az.ingress.hrms.repository.PersonRepository;
import az.ingress.hrms.service.person.PersonService;
import az.ingress.hrms.specification.PersonSpecification;
import az.ingress.hrms.util.PaginationUtils;
import az.ingress.hrms.util.SecurityUtils;
import az.ingress.hrms.util.SortUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonServiceImpl implements PersonService {

    private final PersonRepository repository;
    private final PersonMapper mapper;
    private final StatusHelper statusHelper;
    private final PersonLogService personLogService;

    @Override
    @Transactional
    public PersonResponse create(PersonRequest request) {
        Person person = mapper.toEntity(request);
        person.setStatus(statusHelper.getActive());

        repository.save(person);

        personLogService.log(
                person,
                LogAction.POST,
                SecurityUtils.getCurrentUsername()
        );

        return mapper.toResponse(person);
    }

    @Override
    @Transactional
    public PersonResponse update(Integer id, PersonRequest request) {
        Person person = fetchPerson(id);

        personLogService.log(
                person,
                LogAction.PUT,
                SecurityUtils.getCurrentUsername());

        mapper.updateEntity(person, request);

        repository.save(person);

        return mapper.toResponse(person);
    }

    @Override
    public PersonResponse getById(Integer id) {
        return mapper.toResponse(fetchPerson(id));
    }

    private static final Set<String> SORTABLE_FIELDS =
            Set.of(
                    "id",
                    "firstName",
                    "lastName",
                    "createdAt",
                    "updatedAt"
            );


    @Override
    @Transactional(readOnly = true)
    public PageResponse<PersonResponse> getAll(PersonSearchCriteria criteria, Pageable pageable) {
        Specification<Person> specification = PersonSpecification.build(criteria);
        Page<Person> page = repository.findAll(specification, pageable);

        return PaginationUtils.toPageResponse(page, mapper::toResponse);
    }

    @Override
    @Transactional
    public void softDelete(Integer id) {
        Person person = fetchPerson(id);

        personLogService.log(
                person,
                LogAction.DELETE,
                "ADMIN"
        );

        person.setIsDeleted(true);
        person.setDeletedAt(LocalDateTime.now());
        person.setDeletedBy(SecurityUtils.getCurrentUsername());

        repository.save(person);
    }

    @Override
    @Transactional
    public void restore(Integer id) {
        Person person = repository.findByIdWithDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found."));

        if (!Boolean.TRUE.equals(person.getIsDeleted())) {
            throw new IllegalStateException("Resource is not deleted.");
        }

        personLogService.log(
                person,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()
        );

        person.setIsDeleted(false);
        person.setDeletedAt(null);
        person.setDeletedBy(null);

        repository.save(person);
    }

    @Override
    @Transactional
    public PersonResponse activate(Integer id) {
        Person person = fetchPerson(id);

        personLogService.log(
                person,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()
        );

        person.setStatus(statusHelper.getActive());

        repository.save(person);

        return mapper.toResponse(person);
    }

    @Override
    @Transactional
    public PersonResponse deactivate(Integer id) {
        Person person = fetchPerson(id);

        personLogService.log(
                person,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()
        );

        person.setStatus(statusHelper.getInactive());

        repository.save(person);

        return mapper.toResponse(person);
    }

    private Person fetchPerson(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(s -> {
                                throw new DeletedResourceException("Person is deleted.");
                            });
                    throw new ResourceNotFoundException("Person not found.");
                });
    }
}