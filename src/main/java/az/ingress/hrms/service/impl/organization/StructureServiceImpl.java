package az.ingress.hrms.service.impl.organization;

import az.ingress.hrms.dto.structure.StructureRequest;
import az.ingress.hrms.dto.structure.StructureResponse;
import az.ingress.hrms.entity.organization.Structure;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.ResourceAlreadyExistsException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.mapper.StructureMapper;
import az.ingress.hrms.repository.StructureRepository;
import az.ingress.hrms.service.organization.StructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class StructureServiceImpl implements StructureService {

    private final StructureRepository repository;
    private final StructureMapper mapper;

    @Override
    @Transactional
    public StructureResponse create(StructureRequest request) {

        if (repository.existsByNameIgnoreCase(request.getName())) {
            throw new ResourceAlreadyExistsException(
                    "Structure with name '" + request.getName() + "' already exists."
            );
        }

        Structure structure = mapper.toEntity(request);

        if (request.getParentStructureId() != null) {

            Structure parentStructure = fetchStructure(request.getParentStructureId());

            structure.setParentStructure(parentStructure);
        }

        repository.save(structure);

        return mapper.toResponse(structure);
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

        mapper.updateEntity(structure, request);


        if (request.getParentStructureId() == null) {

            structure.setParentStructure(null);

        } else {

            Structure parent = fetchStructure(request.getParentStructureId());

            if (parent.getId().equals(id)) {
                throw new IllegalArgumentException(
                        "A structure cannot be its own parent."
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

    @Override
    public List<StructureResponse> getAll() {
        return repository
                .findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<StructureResponse> getRootStructures() {
        return repository.findByParentStructureIsNull()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<StructureResponse> getChildren(Integer parentId) {
        Structure structure = fetchStructure(parentId);

        return repository.findByParentStructure(structure)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void softDelete(Integer id) {

        Structure structure = fetchStructure(id);

        if (repository.existsByParentStructure(structure)) {
            throw new IllegalStateException(
                    "Cannot delete a structure that has child structures."
            );
        }

        structure.setIsDeleted(true);
        structure.setDeletedAt(LocalDateTime.now());
        structure.setDeletedBy("SYSTEM");

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
            throw new IllegalStateException("Structure is not deleted.");
        }

        structure.setIsDeleted(false);
        structure.setDeletedAt(null);
        structure.setDeletedBy(null);

        repository.save(structure);
    }

    private Structure fetchStructure(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "Structure is deleted");
                            });

                    throw new ResourceNotFoundException("Structure not found.");
                });
    }
}
