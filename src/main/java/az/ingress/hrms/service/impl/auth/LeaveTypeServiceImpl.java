package az.ingress.hrms.service.impl.auth;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.LeaveTypeSearchCriteria;
import az.ingress.hrms.dto.leaveType.LeaveTypeCreateRequest;
import az.ingress.hrms.dto.leaveType.LeaveTypeResponse;
import az.ingress.hrms.dto.leaveType.LeaveTypeUpdateRequest;
import az.ingress.hrms.entity.lookup.LeaveType;
import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.exception.BadRequestException;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.log.LogAction;
import az.ingress.hrms.log.lookup.leaveType.LeaveTypeLogService;
import az.ingress.hrms.mapper.LeaveTypeMapper;
import az.ingress.hrms.repository.LeaveTypeRepository;
import az.ingress.hrms.service.auth.LeaveTypeService;
import az.ingress.hrms.specification.LeaveTypeSpecification;
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
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeRepository repository;
    private final LeaveTypeMapper mapper;
    private final LeaveTypeLogService leaveTypeLogService;

    @Override
    @Transactional
    public LeaveTypeResponse create(LeaveTypeCreateRequest request) {

        String code = request.getCode().trim().toUpperCase();
        String name = request.getName().trim();

        if (repository.findByCodeIgnoreCase(code).isPresent()) {
            throw new BadRequestException(
                    "Leave type with code '" + code + "' already exists."
            );
        }

        if (repository.existsByNameIgnoreCase(name)) {
            throw new BadRequestException(
                    "Leave type with name '" + name + "' already exists."
            );
        }

        LeaveType entity = mapper.toEntity(request);

        entity.setCode(code);
        entity.setName(name);

        LeaveType savedEntity = repository.save(entity);

        leaveTypeLogService.log(
                entity,
                LogAction.POST,
                SecurityUtils.getCurrentUsername());

        return mapper.toResponse(savedEntity);
    }

    @Override
    @Transactional
    public LeaveTypeResponse update(
            Integer id,
            LeaveTypeUpdateRequest request
    ) {

        LeaveType entity = fetchLeaveType(id);

        String name = request.getName().trim();

        if (!entity.getName().equalsIgnoreCase(name)
                && repository.existsByNameIgnoreCase(name)) {

            throw new BadRequestException(
                    "Leave type with name '" + name + "' already exists."
            );
        }

        leaveTypeLogService.log(
                entity,
                LogAction.PUT,
                SecurityUtils.getCurrentUsername()
        );

        mapper.updateEntity(entity, request);

        entity.setName(name);

        LeaveType updatedEntity = repository.save(entity);

        return mapper.toResponse(updatedEntity);
    }

    @Override
    public LeaveTypeResponse getById(Integer id) {
        return mapper.toResponse(fetchLeaveType(id));
    }

    @Override
    public LeaveTypeResponse getByCode(String code) {
        LeaveType entity = repository.findByCodeIgnoreCase(code.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found with code: " + code));
        return mapper.toResponse(entity);
    }

    @Override
    public PageResponse<LeaveTypeResponse> getAll(LeaveTypeSearchCriteria criteria, Pageable pageable) {
        Specification<LeaveType> specification = LeaveTypeSpecification.build(criteria);
        Page<LeaveType> page = repository.findAll(specification, pageable);

        return PaginationUtils.toPageResponse(page, mapper::toResponse);
    }

    @Override
    @Transactional
    public void softDelete(Integer id) {
        LeaveType entity = fetchLeaveType(id);

        if (entity.getIsDeleted()) {
            throw new BadRequestException(
                    "Leave type is already deleted."
            );
        }

        leaveTypeLogService.log(
                entity,
                LogAction.DELETE,
                SecurityUtils.getCurrentUsername()
        );

        entity.setDeletedBy(SecurityUtils.getCurrentUsername());
        entity.setIsDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());

        repository.save(entity);
    }

    @Override
    @Transactional
    public void restore(Integer id) {
        LeaveType entity = repository.findByIdWithDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deleted leave type not found with id: " + id));

        if (!entity.getIsDeleted()) {
            throw new BadRequestException("Leave type is not deleted.");
        }

        leaveTypeLogService.log(
                entity,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername());

        entity.setIsDeleted(false);
        entity.setDeletedAt(null);
        entity.setDeletedBy(null);

        repository.save(entity);
    }


    private LeaveType fetchLeaveType(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException("Leave type is deleted.");
                            });
                    throw new ResourceNotFoundException("Leave type not found with id: " + id);
                });
    }
}