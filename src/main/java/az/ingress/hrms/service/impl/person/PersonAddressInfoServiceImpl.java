package az.ingress.hrms.service.impl.person;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.person.PersonResponse;
import az.ingress.hrms.dto.personAddressInfo.PersonAddressInfoCreateRequest;
import az.ingress.hrms.dto.personAddressInfo.PersonAddressInfoResponse;
import az.ingress.hrms.dto.personAddressInfo.PersonAddressInfoUpdateRequest;
import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.entity.person.PersonAddressInfo;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.DuplicateResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.helper.StatusHelper;
import az.ingress.hrms.log.LogAction;
import az.ingress.hrms.log.person.personAddressInfo.PersonAddressInfoLogService;
import az.ingress.hrms.mapper.PersonAddressInfoMapper;
import az.ingress.hrms.repository.PersonAddressInfoRepository;
import az.ingress.hrms.repository.PersonRepository;
import az.ingress.hrms.service.person.PersonAddressInfoService;
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

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PersonAddressInfoServiceImpl implements PersonAddressInfoService {

    private final PersonAddressInfoRepository repository;
    private final PersonRepository personRepository;
    private final PersonAddressInfoMapper mapper;
    private final StatusHelper statusHelper;
    private final PersonAddressInfoLogService addressInfoLogService;

    @Override
    @Transactional
    public PersonAddressInfoResponse create(PersonAddressInfoCreateRequest request) {
        Person person = personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Person not found"
                ));

        String address = request.getAddress().trim();

        if (repository.existsByPersonAndAddressIgnoreCase(person, address)) {
            throw new DuplicateResourceException(
                    "This address already exists for the person."
            );
        }

        PersonAddressInfo entity = mapper.toEntity(request);

        entity.setPerson(person);
        entity.setAddress(address);
        entity.setStatus(statusHelper.getActive());

        repository.save(entity);

        addressInfoLogService.log(
                entity,
                LogAction.POST,
                SecurityUtils.getCurrentUsername());

        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public PersonAddressInfoResponse update(
            Integer id, PersonAddressInfoUpdateRequest request) {
        PersonAddressInfo entity = fetchPersonAddressInfo(id);

        String address = request.getAddress().trim();

        if (!entity.getAddress().equalsIgnoreCase(address)) {
            if (repository.existsByPersonAndAddressIgnoreCase(
                    entity.getPerson(), address)) {
                throw new DuplicateResourceException(
                        "This address already exists for the person.");
            }
        }

        addressInfoLogService.log(
                entity,
                LogAction.PUT,
                SecurityUtils.getCurrentUsername()
        );

        mapper.updateEntity(entity, request);

        entity.setAddress(address);
        repository.save(entity);

        return mapper.toResponse(entity);
    }

    @Override
    public PersonAddressInfoResponse getById(Integer id) {
        return mapper.toResponse(fetchPersonAddressInfo(id));
    }

    @Override
    public PageResponse<PersonAddressInfoResponse> getAll(int pageNo, int pageSize) {
        Pageable pageable =
                PageRequest.of(
                        pageNo,
                        pageSize,
                        Sort.by("id").ascending()
                );

        Page<PersonAddressInfo> page =
                repository.findAll(pageable);

        return PaginationUtils.toPageResponse(
                page,
                mapper::toResponse
        );
    }

    @Override
    public PageResponse<PersonAddressInfoResponse> getAllByPerson(Integer personId, int pageNo, int pageSize) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Person not found"
                ));
        Pageable pageable =
                PageRequest.of(
                        pageNo,
                        pageSize,
                        Sort.by("id").ascending()
                );

        Page<PersonAddressInfo> page =
                repository.findByPerson(person, pageable);

        return PaginationUtils.toPageResponse(
                page,
                mapper::toResponse
        );
    }

    @Override
    @Transactional
    public void softDelete(Integer id) {
        PersonAddressInfo entity = fetchPersonAddressInfo(id);

        addressInfoLogService.log(
                entity,
                LogAction.DELETE,
                SecurityUtils.getCurrentUsername());

        entity.setIsDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());
        entity.setDeletedBy(SecurityUtils.getCurrentUsername());

        repository.save(entity);
    }

    @Override
    @Transactional
    public void restore(Integer id) {
        PersonAddressInfo entity = repository.findByIdWithDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address information not found."
                ));

        if (!Boolean.TRUE.equals(entity.getIsDeleted())) {
            throw new IllegalStateException("Resource is not deleted.");
        }

        addressInfoLogService.log(
                entity,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername());

        entity.setIsDeleted(false);
        entity.setDeletedAt(null);
        entity.setDeletedBy(null);

        repository.save(entity);
    }

    @Override
    @Transactional
    public PersonAddressInfoResponse activate(Integer id) {
        PersonAddressInfo entity = fetchPersonAddressInfo(id);

        addressInfoLogService.log(
                entity,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername());

        entity.setStatus(statusHelper.getActive());

        repository.save(entity);

        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public PersonAddressInfoResponse deactivate(Integer id) {
        PersonAddressInfo entity = fetchPersonAddressInfo(id);

        addressInfoLogService.log(
                entity,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername());

        entity.setStatus(statusHelper.getInactive());

        repository.save(entity);

        return mapper.toResponse(entity);
    }

    private PersonAddressInfo fetchPersonAddressInfo(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException("Address information is deleted.");
                            });
                    throw new ResourceNotFoundException("Address information not found.");
                });
    }
}