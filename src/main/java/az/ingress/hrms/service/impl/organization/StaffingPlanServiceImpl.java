package az.ingress.hrms.service.impl.organization;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.StaffingPlanSearchCriteria;
import az.ingress.hrms.dto.staffingPlan.StaffingPlanCreateRequest;
import az.ingress.hrms.dto.staffingPlan.StaffingPlanResponse;
import az.ingress.hrms.dto.staffingPlan.StaffingPlanUpdateRequest;
import az.ingress.hrms.entity.order.Order;
import az.ingress.hrms.entity.organization.Position;
import az.ingress.hrms.entity.organization.StaffingPlan;
import az.ingress.hrms.entity.organization.Structure;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.DuplicateResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.helper.StatusHelper;
import az.ingress.hrms.log.LogAction;
import az.ingress.hrms.log.organization.staffingPlan.StaffingPlanLogService;
import az.ingress.hrms.mapper.organization.StaffingPlanMapper;
import az.ingress.hrms.repository.organization.PositionRepository;
import az.ingress.hrms.repository.organization.StaffingPlanRepository;
import az.ingress.hrms.repository.organization.StructureRepository;
import az.ingress.hrms.service.organization.StaffingPlanService;
import az.ingress.hrms.specification.organization.StaffingPlanSpecification;
import az.ingress.hrms.util.PaginationUtils;
import az.ingress.hrms.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    public StaffingPlanResponse create(
            Order order,
            StaffingPlanCreateRequest request
    ) {
        Structure structure = fetchStructure(request.getStructureId());
        Position position = fetchPosition(request.getPositionId());

        if (repository.existsByStructureAndPosition(structure, position)) {
            throw new DuplicateResourceException(
                    "This staffing plan already exists with this structure and position.");
        }

        StaffingPlan entity = mapper.toEntity(request);

        entity.setOrder(order);
        entity.setPosition(position);
        entity.setStructure(structure);
        entity.setIsClosed(false);
        entity.setStatus(statusHelper.getActive());

        StaffingPlan savedEntity = repository.save(entity);

        staffingPlanLogService.log(
                savedEntity,
                LogAction.POST,
                SecurityUtils.getCurrentUsername()
        );

        return mapper.toResponse(savedEntity);
    }

    @Override
    @Transactional
    public StaffingPlanResponse update(
            Order order,
            StaffingPlanUpdateRequest request
    ) {
        StaffingPlan entity = fetchStaffingPlanByOrderAndPosition(order.getId(), request.getPositionId());

        Structure structure = fetchStructure(request.getStructureId());
        Position position = fetchPosition(request.getPositionId());

        if (!entity.getPosition().getId().equals(request.getPositionId())
                || !entity.getStructure().getId().equals(request.getStructureId())) {

            boolean exists = repository.existsByStructureAndPosition(structure, position);
            if (exists) {
                throw new DuplicateResourceException("Staffing plan already exists");
            }
        }

        staffingPlanLogService.log(
                entity,
                LogAction.PUT,
                SecurityUtils.getCurrentUsername()
        );

        mapper.updateEntity(entity, request);

        entity.setStructure(structure);
        entity.setPosition(position);

        StaffingPlan saved = repository.save(entity);

        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public List<StaffingPlanResponse> activate(Order order) {
        List<StaffingPlan> plans = repository.findAllByOrderId(order.getId());

        for (StaffingPlan entity : plans) {
            staffingPlanLogService.log(
                    entity,
                    LogAction.PATCH,
                    SecurityUtils.getCurrentUsername()
            );

            entity.setStatus(statusHelper.getActive());
        }

        return repository.saveAll(plans)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<StaffingPlanResponse> deactivate(Order order) {
        List<StaffingPlan> plans = repository.findAllByOrderId(order.getId());

        for (StaffingPlan entity : plans) {
            staffingPlanLogService.log(
                    entity,
                    LogAction.PATCH,
                    SecurityUtils.getCurrentUsername()
            );

            entity.setStatus(statusHelper.getInactive());
        }

        return repository.saveAll(plans)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void softDelete(Order order) {
        List<StaffingPlan> plans = repository.findAllByOrderId(order.getId());

        LocalDateTime now = LocalDateTime.now();
        String username = SecurityUtils.getCurrentUsername();

        for (StaffingPlan entity : plans) {
            staffingPlanLogService.log(
                    entity,
                    LogAction.DELETE,
                    username
            );

            entity.setIsDeleted(true);
            entity.setDeletedAt(now);
            entity.setDeletedBy(username);
        }

        repository.saveAll(plans);
    }

    @Override
    @Transactional
    public void restore(Order order) {
        List<StaffingPlan> plans = repository.findAllByOrderIdWithDeleted(order.getId());

        String username = SecurityUtils.getCurrentUsername();

        for (StaffingPlan entity : plans) {
            if (!Boolean.TRUE.equals(entity.getIsDeleted())) {
                continue;
            }

            staffingPlanLogService.log(
                    entity,
                    LogAction.PATCH,
                    username
            );

            entity.setIsDeleted(false);
            entity.setDeletedAt(null);
            entity.setDeletedBy(null);
        }

        repository.saveAll(plans);
    }


    @Override
    public List<StaffingPlanResponse> getByOrderId(Integer orderId) {
        return repository.findByOrderIdAndIsDeletedFalse(orderId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StaffingPlanResponse getById(Integer id) {
        return mapper.toResponse(fetchStaffingPlan(id));
    }

    @Override
    public PageResponse<StaffingPlanResponse> getAll(StaffingPlanSearchCriteria criteria, Pageable pageable) {
        Specification<StaffingPlan> specification = StaffingPlanSpecification.build(criteria);
        Page<StaffingPlan> page = repository.findAll(specification, pageable);

        return PaginationUtils.toPageResponse(page, mapper::toResponse);
    }

    @Override
    @Transactional
    public void close(Integer id) {
        StaffingPlan plan = fetchStaffingPlan(id);

        if (Boolean.TRUE.equals(plan.getIsClosed())) {
            throw new IllegalStateException("Staffing plan is already closed.");
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

        if (!Boolean.TRUE.equals(plan.getIsClosed())) {
            throw new IllegalStateException("Staffing plan is already open.");
        }

        staffingPlanLogService.log(
                plan,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()
        );

        plan.setIsClosed(false);

        repository.save(plan);
    }

    private StaffingPlan fetchStaffingPlan(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException("Staffing Plan is deleted.");
                            });

                    throw new ResourceNotFoundException("Staffing Plan not found.");
                });
    }

    private StaffingPlan fetchStaffingPlanByOrderAndPosition(Integer orderId, Integer positionId) {
        return repository.findByOrderIdAndPositionId(orderId, positionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Staffing Plan not found for position ID " + positionId + " in order ID " + orderId
                ));
    }

    private Structure fetchStructure(Integer id) {
        return structureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Structure not found with id: " + id));
    }

    private Position fetchPosition(Integer id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found with id: " + id));
    }
}