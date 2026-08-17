package az.ingress.hrms.service.impl.person;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.person.PersonResponse;
import az.ingress.hrms.dto.personContactInfo.PersonContactInfoCreateRequest;
import az.ingress.hrms.dto.personContactInfo.PersonContactInfoResponse;
import az.ingress.hrms.dto.personContactInfo.PersonContactInfoUpdateRequest;
import az.ingress.hrms.entity.lookup.ContactType;
import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.entity.person.PersonContactInfo;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.DuplicateResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.helper.StatusHelper;
import az.ingress.hrms.log.LogAction;
import az.ingress.hrms.log.person.personContactInfo.PersonContactInfoLogService;
import az.ingress.hrms.mapper.PersonContactInfoMapper;
import az.ingress.hrms.repository.ContactTypeRepository;
import az.ingress.hrms.repository.PersonContactInfoRepository;
import az.ingress.hrms.repository.PersonRepository;
import az.ingress.hrms.service.person.PersonContactInfoService;
import az.ingress.hrms.util.PaginationUtils;
import az.ingress.hrms.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonContactInfoServiceImpl implements PersonContactInfoService {

    private final PersonContactInfoMapper mapper;
    private final PersonContactInfoRepository repository;
    private final PersonRepository personRepository;
    private final ContactTypeRepository contactTypeRepository;
    private final StatusHelper statusHelper;
    private final PersonContactInfoLogService contactInfoLogService;

    @Override
    @Transactional
    public PersonContactInfoResponse create(PersonContactInfoCreateRequest request) {
        Person person = personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Person not found."
                ));

        ContactType contactType = contactTypeRepository.findById(request.getContactTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contact Type not found."
                ));

        String contactValue = request.getContactValue().trim();

        if (repository.existsByPersonAndContactTypeAndContactValueIgnoreCase(person, contactType, contactValue)) {
            throw new DuplicateResourceException("This contact info already exists for this person.");
        }

        PersonContactInfo entity = mapper.toEntity(request);

        entity.setPerson(person);
        entity.setContactValue(contactValue);
        entity.setContactType(contactType);
        entity.setStatus(statusHelper.getActive());

        repository.save(entity);

        contactInfoLogService.log(
                entity,
                LogAction.POST,
                SecurityUtils.getCurrentUsername()        );

        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public PersonContactInfoResponse update(
            Integer id, PersonContactInfoUpdateRequest request) {

        PersonContactInfo entity = fetchPersonContactInfo(id);

        ContactType contactType = contactTypeRepository.findById(request.getContactTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contact Type not found with ID: " + request.getContactTypeId()
                ));

        String trimmedValue = request.getContactValue().trim();

        if (!entity.getContactValue().equalsIgnoreCase(trimmedValue)
                || !entity.getContactType().getId().equals(request.getContactTypeId())) {

            boolean exists = repository.existsByPersonAndContactTypeAndContactValueIgnoreCase(
                    entity.getPerson(),
                    contactType,
                    trimmedValue
            );

            if (exists) {
                throw new DuplicateResourceException("Contact already exists.");
            }
        }

        contactInfoLogService.log(
                entity,
                LogAction.PUT,
                SecurityUtils.getCurrentUsername()        );

        mapper.updateEntity(entity, request);

        entity.setContactType(contactType);
        entity.setContactValue(trimmedValue);

        repository.save(entity);

        return mapper.toResponse(entity);
    }

    @Override
    public PersonContactInfoResponse getById(Integer id) {
        return mapper.toResponse(fetchPersonContactInfo(id));
    }

    @Override
    public PageResponse<PersonContactInfoResponse> getAll(int pageNo, int pageSize) {
        Pageable pageable =
                PageRequest.of(
                        pageNo,
                        pageSize,
                        Sort.by("id").ascending()
                );

        Page<PersonContactInfo> page =
                repository.findAll(pageable);

        return PaginationUtils.toPageResponse(
                page,
                mapper::toResponse
        );
    }

    @Override
    public PageResponse<PersonContactInfoResponse> getAllByPerson(Integer personId, int pageNo, int pageSize) {
        if (!personRepository.existsById(personId)) {
            throw new ResourceNotFoundException(
                    "Person with ID " + personId + " does not exist."
            );
        }
        Pageable pageable =
                PageRequest.of(
                        pageNo,
                        pageSize,
                        Sort.by("id").ascending()
                );

        Page<PersonContactInfo> page =
                repository.findByPersonId(personId, pageable);

        return PaginationUtils.toPageResponse(
                page,
                mapper::toResponse
        );
    }

    @Override
    @Transactional
    public void softDelete(Integer id) {
        PersonContactInfo entity = fetchPersonContactInfo(id);

        contactInfoLogService.log(
                entity,
                LogAction.DELETE,
                SecurityUtils.getCurrentUsername()        );

        entity.setIsDeleted(true);
        entity.setDeletedBy(SecurityUtils.getCurrentUsername());
        entity.setDeletedAt(LocalDateTime.now());

        repository.save(entity);
    }

    @Override
    @Transactional
    public void restore(Integer id) {
        PersonContactInfo entity = repository.findByIdWithDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contact info not found."
                ));

        if (!Boolean.TRUE.equals(entity.getIsDeleted())) {
            throw new IllegalStateException("Contact info is not deleted.");
        }

        contactInfoLogService.log(
                entity,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()        );

        entity.setIsDeleted(false);
        entity.setDeletedAt(null);
        entity.setDeletedBy(null);

        repository.save(entity);
    }

    @Override
    @Transactional
    public PersonContactInfoResponse activate(Integer id) {
        PersonContactInfo entity = fetchPersonContactInfo(id);

        contactInfoLogService.log(
                entity,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()        );

        entity.setStatus(statusHelper.getActive());

        repository.save(entity);

        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public PersonContactInfoResponse deactivate(Integer id) {
        PersonContactInfo entity = fetchPersonContactInfo(id);

        contactInfoLogService.log(
                entity,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()        );

        entity.setStatus(statusHelper.getInactive());

        repository.save(entity);

        return mapper.toResponse(entity);
    }

    private PersonContactInfo fetchPersonContactInfo(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "Person Contact info is deleted"
                                );
                            });

                    throw new ResourceNotFoundException("Person contact information not found.");
                });
    }
}