package az.ingress.hrms.service.impl.organization;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.structure.StructureRequest;
import az.ingress.hrms.dto.structure.StructureResponse;
import az.ingress.hrms.entity.organization.Structure;
import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.ResourceAlreadyExistsException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.helper.StatusHelper;
import az.ingress.hrms.log.LogAction;
import az.ingress.hrms.log.organization.structure.StructureLogService;
import az.ingress.hrms.mapper.StructureMapper;
import az.ingress.hrms.repository.StructureRepository;
import az.ingress.hrms.service.organization.StructureService;
import az.ingress.hrms.specification.StructureSpecification;
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
import java.util.List;
import java.util.Set;

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
    public StructureResponse create(StructureRequest request) {

        if (repository.existsByNameIgnoreCase(request.getName())) {
            throw new ResourceAlreadyExistsException(
                    "Structure with name '" + request.getName() + "' already exists."
            );
        }

        Structure entity = mapper.toEntity(request);

        if (request.getParentStructureId() != null) {

            Structure parentStructure =
                    fetchStructure(request.getParentStructureId());

            if (entity.getId() != null
                    && entity.getId().equals(parentStructure.getId())) {
                throw new IllegalArgumentException(
                        "A structure cannot be its own parent."
                );
            }

            entity.setParentStructure(parentStructure);
        }

        entity.setStatus(statusHelper.getActive());
        repository.save(entity);

        structureLogService.log(
                entity,
                LogAction.POST,
                SecurityUtils.getCurrentUsername()
        );

        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public StructureResponse update(Integer id, StructureRequest request) {

        Structure structure = fetchStructure(id);

        if (!structure.getName().equalsIgnoreCase(request.getName())
                && repository.existsByNameIgnoreCase(request.getName())) {

            throw new ResourceAlreadyExistsException(
                    "Structure with name '" + request.getName() + "' already exists."
            );
        }

        structureLogService.log(
                structure,
                LogAction.PUT,
                SecurityUtils.getCurrentUsername());

        mapper.updateEntity(structure, request);

        if (request.getParentStructureId() == null) {

            structure.setParentStructure(null);

        } else {

            Structure parent =
                    fetchStructure(request.getParentStructureId());

            // protect against direct self-parenting
            if (parent.getId().equals(id)) {
                throw new IllegalArgumentException(
                        "A structure cannot be its own parent."
                );
            }

            // protect against multi-level circular hierarchy
            if (isDescendant(parent, structure)) {
                throw new IllegalArgumentException(
                        "Cannot assign a descendant structure as parent because it would create a circular hierarchy."
                );
            }

            structure.setParentStructure(parent);
        }

        repository.save(structure);

        return mapper.toResponse(structure);
    }

    @Override
    public StructureResponse getById(Integer id) {
        return mapper.toResponse(fetchStructure(id));
    }

    private static final Set<String> SORTABLE_FIELDS =
            Set.of(
                    "id",
                    "name",
                    "isClosed",
                    "createdAt",
                    "updatedAt"
            );

    @Override
    public PageResponse<StructureResponse> getAll(
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir,
            String search,
            Integer parentId,
            Boolean isClosed,
            String status,
            LocalDateTime createdFrom,
            LocalDateTime createdTo,
            Boolean isRoot
    ) {

        Sort sort = SortUtils.buildSort(
                sortBy,
                sortDir,
                SORTABLE_FIELDS,
                "id"
        );

        Pageable pageable = PageRequest.of(
                pageNo,
                pageSize,
                sort
        );

        Specification<Structure> specification = Specification
                .where(StructureSpecification.search(search))
                .and(StructureSpecification.hasParentId(parentId))
                .and(StructureSpecification.isClosed(isClosed))
                .and(StructureSpecification.hasStatusCode(status))
                .and(StructureSpecification.createdFrom(createdFrom))
                .and(StructureSpecification.createdTo(createdTo))
                .and(StructureSpecification.isRoot(isRoot));

        Page<Structure> page = repository.findAll(
                specification,
                pageable
        );

        return PaginationUtils.toPageResponse(
                page,
                mapper::toResponse
        );
    }

//    @Override
//    public PageResponse<StructureResponse> getRootStructures(int pageNo, int pageSize) {
//        Pageable pageable =
//                PageRequest.of(
//                        pageNo,
//                        pageSize,
//                        Sort.by("id").ascending()
//                );
//
//        Page<Structure> page =
//                repository.findByParentStructureIsNull(pageable);
//
//        return PaginationUtils.toPageResponse(
//                page,
//                mapper::toResponse
//        );
//    }
//
//    @Override
//    public PageResponse<StructureResponse> getChildren(Integer parentId, int pageNo, int pageSize) {
//        Structure structure = fetchStructure(parentId);
//        Pageable pageable =
//                PageRequest.of(
//                        pageNo,
//                        pageSize,
//                        Sort.by("id").ascending()
//                );
//
//        Page<Structure> page =
//                repository.findByParentStructure(structure, pageable);
//
//        return PaginationUtils.toPageResponse(
//                page,
//                mapper::toResponse
//        );
//    }

    @Override
    @Transactional
    public void softDelete(Integer id) {

        Structure structure = fetchStructure(id);

        // EXISTING: child protection remains unchanged
        if (repository.existsByParentStructure(structure)) {
            throw new IllegalStateException(
                    "Cannot delete a structure that has child structures."
            );
        }

        structureLogService.log(
                structure,
                LogAction.DELETE,
                SecurityUtils.getCurrentUsername());

        structure.setIsDeleted(true);
        structure.setDeletedAt(LocalDateTime.now());
        structure.setDeletedBy(SecurityUtils.getCurrentUsername());

        repository.save(structure);
    }

    @Override
    @Transactional
    public void restore(Integer id) {

        Structure structure = repository.findByIdWithDeleted(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Structure not found."
                        ));

        if (!Boolean.TRUE.equals(structure.getIsDeleted())) {
            throw new IllegalStateException(
                    "Structure is not deleted."
            );
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

    @Override
    @Transactional
    public StructureResponse activate(Integer id) {

        Structure structure = fetchStructure(id);

        structureLogService.log(
                structure,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername());

        structure.setStatus(statusHelper.getActive());

        repository.save(structure);

        return mapper.toResponse(structure);
    }

    @Override
    @Transactional
    public StructureResponse deactivate(Integer id) {

        Structure structure = fetchStructure(id);

        structureLogService.log(
                structure,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername());

        structure.setStatus(statusHelper.getInactive());

        repository.save(structure);

        return mapper.toResponse(structure);
    }

    // : checks whether 'potentialParent' is already
    // somewhere below 'structure' in the hierarchy.
    //
    // Example:
    //
    // A
    // └── B
    //     └── C
    //
    // If we try to make A's parent = C,
    // this method detects the cycle:
    //
    // A -> C -> B -> A
    //
    private boolean isDescendant(
            Structure potentialParent,
            Structure structure
    ) {

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
                                throw new DeletedResourceException(
                                        "Structure is deleted"
                                );
                            });

                    throw new ResourceNotFoundException(
                            "Structure not found."
                    );
                });
    }
}