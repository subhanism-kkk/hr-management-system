package az.ingress.hrms.service.impl.order;

import az.ingress.hrms.dto.leaveType.LeaveTypeCreateRequest;
import az.ingress.hrms.dto.leaveType.LeaveTypeResponse;
import az.ingress.hrms.dto.leaveType.LeaveTypeUpdateRequest;
import az.ingress.hrms.entity.lookup.LeaveType;
import az.ingress.hrms.exception.BadRequestException;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.mapper.LeaveTypeMapper;
import az.ingress.hrms.repository.LeaveTypeRepository;
import az.ingress.hrms.service.order.LeaveTypeService;
import lombok.RequiredArgsConstructor;
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
    public List<LeaveTypeResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
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

        entity.setDeletedBy("SYSTEM");
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

        entity.setIsDeleted(false);
        entity.setDeletedAt(null);
        entity.setDeletedBy(null);

        repository.save(entity);
    }

    // =========================================================================
    // Private Helper Fetch Methods
    // =========================================================================

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