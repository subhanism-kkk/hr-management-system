package az.ingress.hrms.service.impl.organization;

import az.ingress.hrms.dto.StaffingPlanCreateRequest;
import az.ingress.hrms.dto.StaffingPlanResponse;
import az.ingress.hrms.dto.StaffingPlanUpdateRequest;
import az.ingress.hrms.entity.organization.Position;
import az.ingress.hrms.entity.organization.StaffingPlan;
import az.ingress.hrms.entity.organization.Structure;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.DuplicateResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.mapper.StaffingPlanMapper;
import az.ingress.hrms.repository.PositionRepository;
import az.ingress.hrms.repository.StaffingPlanRepository;
import az.ingress.hrms.repository.StructureRepository;
import az.ingress.hrms.service.organization.StaffingPlanService;
import lombok.RequiredArgsConstructor;
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

        repository.save(entity);

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
    public List<StaffingPlanResponse> getAll() {
        return repository
                .findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<StaffingPlanResponse> getByStructure(Integer structureId) {
        return repository.
                findByStructure(fetchStructure(structureId))
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<StaffingPlanResponse> getByPosition(Integer positionId) {
        return repository.
                findByPosition(fetchPosition(positionId))
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public void close(Integer id) {

        StaffingPlan plan = fetchStaffingPlan(id);

        if (plan.getIsClosed()) {
            throw new IllegalStateException(
                    "Staffing plan is already closed."
            );
        }

        plan.setIsClosed(true);

        repository.save(plan);
    }

    @Override
    public void reopen(Integer id) {
        StaffingPlan plan = fetchStaffingPlan(id);

        if (plan.getIsClosed()) {
            throw new IllegalStateException(
                    "Staffing plan is already open."
            );
        }

        plan.setIsClosed(false);

        repository.save(plan);
    }

    @Override
    public void softDelete(Integer id) {

        StaffingPlan entity = fetchStaffingPlan(id);

        entity.setIsDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());
        entity.setDeletedBy("SYSTEM");

        repository.save(entity);
    }

    @Override
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

        entity.setIsDeleted(false);
        entity.setDeletedAt(null);
        entity.setDeletedBy(null);

        repository.save(entity);

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
