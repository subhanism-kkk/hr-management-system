package az.ingress.hrms.service.impl.person;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.PersonPersonalInfoSearchCriteria;
import az.ingress.hrms.dto.person.PersonResponse;
import az.ingress.hrms.dto.personPersonalInfo.PersonPersonalInfoCreateRequest;
import az.ingress.hrms.dto.personPersonalInfo.PersonPersonalInfoResponse;
import az.ingress.hrms.dto.personPersonalInfo.PersonPersonalInfoUpdateRequest;
import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.entity.person.PersonPersonalInfo;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.DuplicateResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.helper.StatusHelper;
import az.ingress.hrms.log.LogAction;
import az.ingress.hrms.log.person.personPersonalInfo.PersonPersonalInfoLogService;
import az.ingress.hrms.mapper.PersonPersonalInfoMapper;
import az.ingress.hrms.repository.PersonPersonalInfoRepository;
import az.ingress.hrms.repository.PersonRepository;
import az.ingress.hrms.service.person.PersonPersonalInfoService;
import az.ingress.hrms.specification.PersonPersonalInfoSpecification;
import az.ingress.hrms.util.PaginationUtils;
import az.ingress.hrms.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonPersonalInfoServiceImpl implements PersonPersonalInfoService {

    private final PersonPersonalInfoRepository repository;
    private final PersonRepository personRepository;
    private final PersonPersonalInfoMapper mapper;
    private final StatusHelper statusHelper;
    private final PersonPersonalInfoLogService personalInfoLogService;

    @Override
    @Transactional
    public PersonPersonalInfoResponse create(PersonPersonalInfoCreateRequest request) {
        Person person = personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));

        if (repository.existsByPerson(person)) {
            throw new DuplicateResourceException("This person already has personal information.");
        }

        String finCode = request.getFinCode().trim().toUpperCase();

        if (repository.existsByFinCode(finCode)) {
            throw new DuplicateResourceException("FIN code already exists.");
        }

        PersonPersonalInfo entity = mapper.toEntity(request);

        entity.setPerson(person);
        entity.setFinCode(finCode);
        entity.setStatus(statusHelper.getActive());

        repository.save(entity);

        personalInfoLogService.log(
                entity,
                LogAction.POST,
                SecurityUtils.getCurrentUsername()
        );

        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public PersonPersonalInfoResponse update(Integer id, PersonPersonalInfoUpdateRequest request) {
        PersonPersonalInfo entity = fetchPersonPersonalInfo(id);

        personalInfoLogService.log(
                entity,
                LogAction.PUT,
                SecurityUtils.getCurrentUsername()
        );

        mapper.updateEntity(entity, request);

        repository.save(entity);

        return mapper.toResponse(entity);
    }

    @Override
    public PersonPersonalInfoResponse getById(Integer id) {
        PersonPersonalInfo entity = fetchPersonPersonalInfo(id);
        return mapper.toResponse(entity);
    }

    @Override
    public PageResponse<PersonPersonalInfoResponse> getAll(PersonPersonalInfoSearchCriteria criteria, Pageable pageable) {
        Specification<PersonPersonalInfo> specification = PersonPersonalInfoSpecification.build(criteria);
        Page<PersonPersonalInfo> page = repository.findAll(specification, pageable);

        return PaginationUtils.toPageResponse(page, mapper::toResponse);
    }

    @Override
    @Transactional
    public void softDelete(Integer id) {
        PersonPersonalInfo entity = fetchPersonPersonalInfo(id);

        personalInfoLogService.log(
                entity,
                LogAction.DELETE,
                SecurityUtils.getCurrentUsername()
        );

        entity.setIsDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());
        entity.setDeletedBy(SecurityUtils.getCurrentUsername());

        repository.save(entity);
    }

    @Override
    @Transactional
    public void restore(Integer id) {
        PersonPersonalInfo entity = repository.findByIdWithDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personal information not found."));

        if (!Boolean.TRUE.equals(entity.getIsDeleted())) {
            throw new IllegalStateException("Personal information is not deleted.");
        }

        personalInfoLogService.log(
                entity,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()
        );

        entity.setIsDeleted(false);
        entity.setDeletedAt(null);
        entity.setDeletedBy(null);

        repository.save(entity);
    }

    @Override
    @Transactional
    public PersonPersonalInfoResponse activate(Integer id) {
        PersonPersonalInfo entity = fetchPersonPersonalInfo(id);

        personalInfoLogService.log(
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
    public PersonPersonalInfoResponse deactivate(Integer id) {
        PersonPersonalInfo entity = fetchPersonPersonalInfo(id);

        personalInfoLogService.log(
                entity,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()        );

        entity.setStatus(statusHelper.getInactive());

        repository.save(entity);

        return mapper.toResponse(entity);
    }

    private PersonPersonalInfo fetchPersonPersonalInfo(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(s -> {
                                throw new DeletedResourceException("Person Personal Info is deleted.");
                            });
                    throw new ResourceNotFoundException("Person Personal Info not found.");
                });
    }
}