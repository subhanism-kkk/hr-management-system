package az.ingress.hrms.service.impl.organization;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.StructureSearchCriteria;
import az.ingress.hrms.dto.structure.StructureRequest;
import az.ingress.hrms.dto.structure.StructureResponse;
import az.ingress.hrms.entity.order.Order;
import az.ingress.hrms.entity.organization.Structure;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.ResourceAlreadyExistsException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.helper.StatusHelper;
import az.ingress.hrms.log.LogAction;
import az.ingress.hrms.log.organization.structure.StructureLogService;
import az.ingress.hrms.mapper.organization.StructureMapper;
import az.ingress.hrms.repository.organization.StructureRepository;
import az.ingress.hrms.service.organization.StructureService;
import az.ingress.hrms.specification.organization.StructureSpecification;
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
public class StructureServiceImpl implements StructureService {

    private final StructureRepository repository;
    private final StructureMapper mapper;
    private final StatusHelper statusHelper;
    private final StructureLogService structureLogService;

    @Override
    @Transactional
    public StructureResponse create(Order order, StructureRequest request) {

        if (repository.existsByNameIgnoreCase(request.getName())) {
            throw new ResourceAlreadyExistsException(
                    "Structure with name '" + request.getName() + "' already exists."
            );
        }

        Structure entity = mapper.toEntity(request);
        entity.setOrder(order);

        if (request.getParentStructureId() != null) {
            Structure parentStructure = fetchStructure(request.getParentStructureId());

            if (entity.getId() != null && entity.getId().equals(parentStructure.getId())) {
                throw new IllegalArgumentException("A structure cannot be its own parent.");
            }

            entity.setParentStructure(parentStructure);
        }

        entity.setStatus(statusHelper.getActive());
        Structure saved = repository.save(entity);

        structureLogService.log(
                saved,
                LogAction.POST,
                SecurityUtils.getCurrentUsername()
        );

        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public StructureResponse update(Order order, StructureRequest request) {

        Structure structure = fetchStructureByOrder(order.getId());

        if (!structure.getName().equalsIgnoreCase(request.getName())
                && repository.existsByNameIgnoreCase(request.getName())) {
            throw new ResourceAlreadyExistsException(
                    "Structure with name '" + request.getName() + "' already exists."
            );
        }

        structureLogService.log(
                structure,
                LogAction.PUT,
                SecurityUtils.getCurrentUsername()
        );

        mapper.updateEntity(structure, request);

        if (request.getParentStructureId() == null) {
            structure.setParentStructure(null);
        } else {
            Structure parent = fetchStructure(request.getParentStructureId());
            // protect against direct self-parenting
            if (parent.getId().equals(structure.getId())) {
                throw new IllegalArgumentException("A structure cannot be its own parent.");
            }
            // protect against multi-level circular hierarchy
            if (isDescendant(parent, structure)) {
                throw new IllegalArgumentException(
                        "Cannot assign a descendant structure as parent because it would create a circular hierarchy."
                );
            }

            structure.setParentStructure(parent);
        }

        Structure saved = repository.save(structure);

        return mapper.toResponse(saved);
    }
    @Override
    public List<StructureResponse> getByOrderId(Integer orderId) {
        return repository.findByOrderId(orderId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<StructureResponse> getAll(StructureSearchCriteria criteria, Pageable pageable) {
        Specification<Structure> specification = StructureSpecification.build(criteria);
        Page<Structure> page = repository.findAll(specification, pageable);

        return PaginationUtils.toPageResponse(page, mapper::toResponse);
    }

    @Override
    @Transactional
    public List<StructureResponse> activate(Order order) {
        List<Structure> structures = repository.findAllByOrderId(order.getId());

        for (Structure entity: structures) {
            structureLogService.log(
                    entity,
                    LogAction.PATCH,
                    SecurityUtils.getCurrentUsername()
            );

            entity.setStatus(statusHelper.getActive());
        }
        return repository.saveAll(structures)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<StructureResponse> deactivate(Order order) {
        List<Structure> structures = repository.findAllByOrderId(order.getId());

        for (Structure entity: structures) {
            structureLogService.log(
                    entity,
                    LogAction.PATCH,
                    SecurityUtils.getCurrentUsername()
            );

            entity.setStatus(statusHelper.getInactive());
        }
        return repository.saveAll(structures)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void softDelete(Order order) {
        Structure structure = fetchStructureByOrder(order.getId());

        if (repository.existsByParentStructure(structure)) {
            throw new IllegalStateException("Cannot delete a structure that has child structures.");
        }

        structureLogService.log(
                structure,
                LogAction.DELETE,
                SecurityUtils.getCurrentUsername()
        );

        structure.setIsDeleted(true);
        structure.setDeletedAt(LocalDateTime.now());
        structure.setDeletedBy(SecurityUtils.getCurrentUsername());

        repository.save(structure);
    }

    @Override
    @Transactional
    public void restore(Order order) {
        Structure structure = repository.findByOrderIdWithDeleted(order.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Structure not found for order ID: " + order.getId()));

        if (!Boolean.TRUE.equals(structure.getIsDeleted())) {
            throw new IllegalStateException("Structure is not deleted.");
        }

        structureLogService.log(
                structure,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()
        );

        structure.setIsDeleted(false);
        structure.setDeletedAt(null);
        structure.setDeletedBy(null);

        repository.save(structure);
    }

    private boolean isDescendant(Structure potentialParent, Structure structure) {
        Structure current = potentialParent;
        while (current != null) {
            if (current.getId().equals(structure.getId())) {
                return true;
            }
            current = current.getParentStructure();
        }
        return false;
    }

    private Structure fetchStructure(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException("Structure is deleted.");
                            });
                    throw new ResourceNotFoundException("Structure not found.");
                });
    }

    private Structure fetchStructureByOrder(Integer orderId) {
        return repository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Structure not found for order ID: " + orderId));
    }
}