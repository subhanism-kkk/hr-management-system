package az.ingress.hrms.service.impl.organization;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.staffingPlan.StaffingPlanCreateRequest;
import az.ingress.hrms.dto.staffingPlan.StaffingPlanResponse;
import az.ingress.hrms.dto.staffingPlan.StaffingPlanUpdateRequest;
import az.ingress.hrms.entity.organization.Position;
import az.ingress.hrms.entity.organization.StaffingPlan;
import az.ingress.hrms.entity.organization.Structure;
import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.DuplicateResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.helper.StatusHelper;
import az.ingress.hrms.log.LogAction;
import az.ingress.hrms.log.organization.staffingPlan.StaffingPlanLogService;
import az.ingress.hrms.mapper.StaffingPlanMapper;
import az.ingress.hrms.repository.PositionRepository;
import az.ingress.hrms.repository.StaffingPlanRepository;
import az.ingress.hrms.repository.StructureRepository;
import az.ingress.hrms.service.organization.StaffingPlanService;
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
public class StaffingPlanServiceImpl implements StaffingPlanService {

    private final StaffingPlanMapper mapper;
    private final StaffingPlanRepository repository;
    private final PositionRepository positionRepository;
    private final StructureRepository structureRepository;
    private final StatusHelper statusHelper;
    private final StaffingPlanLogService staffingPlanLogService;

    @Override
    @Transactional
    public StaffingPlanResponse create(StaffingPlanCreateRequest request) {
        Structure structure = fetchStructure(request.getStructureId());

        Position position = fetchPosition(request.getPositionId());

        if (repository.existsByStructureAndPosition(structure, position)) {
            throw new DuplicateResourceException(
                    "This staffing plan already exists with this structure and position.");
        }
        StaffingPlan entity = mapper.toEntity(request);

        entity.setPosition(position);
        entity.setStructure(structure);
        entity.setStatus(statusHelper.getActive());

        repository.save(entity);

        staffingPlanLogService.log(
                entity,
                LogAction.POST,
                SecurityUtils.getCurrentUsername());

        return mapper.toResponse(entity);

    }

    @Override
    @Transactional
    public StaffingPlanResponse update(Integer id, StaffingPlanUpdateRequest request) {
        StaffingPlan entity = fetchStaffingPlan(id);

        Structure structure = fetchStructure(request.getStructureId());

        Position position = fetchPosition(request.getPositionId());

        if (!entity.getPosition().getId()
                .equals(request.getPositionId())
                ||
                !entity.getStructure().getId()
                        .equals(request.getStructureId())
        ) {

            boolean exists =
                    repository.existsByStructureAndPosition(
                            structure, position);
            if (exists) {
                throw new DuplicateResourceException(
                        "Staffing plan already exists"
                );
            }
        }

        staffingPlanLogService.log(
                entity,
                LogAction.PUT,
                SecurityUtils.getCurrentUsername());

        mapper.updateEntity(entity, request);

        entity.setStructure(structure);
        entity.setPosition(position);


        repository.save(entity);

        return mapper.toResponse(entity);
    }

    @Override
    public StaffingPlanResponse getById(Integer id) {
        return mapper.toResponse(fetchStaffingPlan(id));
    }

    @Override
    public PageResponse<StaffingPlanResponse> getAll(int pageNo, int pageSize) {
        Pageable pageable =
                PageRequest.of(
                        pageNo,
                        pageSize,
                        Sort.by("id").ascending()
                );

        Page<StaffingPlan> page =
                repository.findAll(pageable);

        return PaginationUtils.toPageResponse(
                page,
                mapper::toResponse
        );
    }

    @Override
    public PageResponse<StaffingPlanResponse> getByStructure(Integer structureId, int pageNo, int pageSize) {
        Pageable pageable =
                PageRequest.of(
                        pageNo,
                        pageSize,
                        Sort.by("id").ascending()
                );

        Page<StaffingPlan> page =
                repository.findByStructure(fetchStructure(structureId), pageable);

        return PaginationUtils.toPageResponse(
                page,
                mapper::toResponse
        );
    }

    @Override
    public PageResponse<StaffingPlanResponse> getByPosition(Integer positionId, int pageNo, int pageSize) {
        Pageable pageable =
                PageRequest.of(
                        pageNo,
                        pageSize,
                        Sort.by("id").ascending()
                );

        Page<StaffingPlan> page =
                repository.findByPosition(fetchPosition(positionId), pageable);

        return PaginationUtils.toPageResponse(
                page,
                mapper::toResponse
        );
    }

    @Override
    @Transactional
    public void close(Integer id) {

        StaffingPlan plan = fetchStaffingPlan(id);

        if (plan.getIsClosed()) {
            throw new IllegalStateException(
                    "Staffing plan is already closed."
            );
        }

        staffingPlanLogService.log(
                plan,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()
        );

        plan.setIsClosed(true);

        repository.save(plan);
    }

    @Override
    @Transactional
    public void reopen(Integer id) {
        StaffingPlan plan = fetchStaffingPlan(id);

        if (!plan.getIsClosed()) {
            throw new IllegalStateException(
                    "Staffing plan is already open."
            );
        }

        staffingPlanLogService.log(
                plan,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername());

        plan.setIsClosed(false);

        repository.save(plan);
    }

    @Override
    @Transactional
    public void softDelete(Integer id) {

        StaffingPlan entity = fetchStaffingPlan(id);

        staffingPlanLogService.log(
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

        StaffingPlan entity = repository.findByIdWithDeleted(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Staffing plan not found."
                        ));

        if (!entity.getIsDeleted()) {
            throw new IllegalStateException(
                    "Staffing plan is not deleted."
            );
        }

        staffingPlanLogService.log(
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
    public StaffingPlanResponse activate(Integer id) {
        StaffingPlan entity = fetchStaffingPlan(id);

        staffingPlanLogService.log(
                entity,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername());

        entity.setStatus(statusHelper.getActive());

        repository.save(entity);

        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public StaffingPlanResponse deactivate(Integer id) {
        StaffingPlan entity = fetchStaffingPlan(id);

        staffingPlanLogService.log(
                entity,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername());

        entity.setStatus(statusHelper.getInactive());

        repository.save(entity);

        return mapper.toResponse(entity);
    }

    private StaffingPlan fetchStaffingPlan(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "Staffing Plan is deleted");
                            });

                    throw new ResourceNotFoundException("Staffing Plan not found.");
                });

    }

    private Structure fetchStructure(Integer id) {
        return structureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Structure not found with id: " + id));

    }

    private Position fetchPosition(Integer id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Position not found with id: " + id));

    }

}
