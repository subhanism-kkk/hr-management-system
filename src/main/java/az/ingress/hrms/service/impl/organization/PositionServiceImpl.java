package az.ingress.hrms.service.impl.organization;

import az.ingress.hrms.entity.organization.Position;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.mapper.PositionMapper;
import az.ingress.hrms.repository.PositionRepository;
import az.ingress.hrms.service.organization.PositionService;
import az.ingress.hrms.dto.position.PositionRequest;
import az.ingress.hrms.dto.position.PositionResponse;

import az.ingress.hrms.exception.ResourceAlreadyExistsException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PositionServiceImpl  implements PositionService {

    private final PositionRepository repository;
    private final PositionMapper mapper;

    @Override
    @Transactional
    public PositionResponse create(PositionRequest request) {
        if (repository.existsByNameIgnoreCase(request.getName())) {
            throw new ResourceAlreadyExistsException(
                    "Position with name '" + request.getName() + "' already exists."
            );
        }
        Position position = mapper.toEntity(request);

        repository.save(position);

        return mapper.toResponse(position);    }

    @Override
    @Transactional
    public PositionResponse update(Integer id, PositionRequest request) {

        Position position = fetchPosition(id);

        if (!position.getName().equalsIgnoreCase(request.getName())
                && repository.existsByNameIgnoreCase(request.getName())) {

            throw new ResourceAlreadyExistsException(
                    "position with name '" + request.getName() + "' already exists."
            );
        }

        mapper.updateEntity(position, request);

        repository.save(position);

        return mapper.toResponse(position);
    }

    @Override
    public PositionResponse getById(Integer id) {
        Position position = fetchPosition(id);
        return mapper.toResponse(position);    }

    @Override
    public List<PositionResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();    }

    @Override
    @Transactional
    public void softDelete(Integer id) {

        Position position = fetchPosition(id);

        position.setIsDeleted(true);
        position.setDeletedAt(LocalDateTime.now());

        position.setDeletedBy("SYSTEM");

        repository.save(position);

    }


    @Override
    @Transactional
    public void restore(Integer id) {

        Position position = repository.findByIdWithDeleted(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "position not found."));

        if (!Boolean.TRUE.equals(position.getIsDeleted())) {
            throw new IllegalStateException("Position is not deleted.");
        }

        position.setIsDeleted(false);
        position.setDeletedAt(null);
        position.setDeletedBy(null);

        repository.save(position);
    }

    private Position fetchPosition(Integer id){
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "Position is deleted");
                            });

                    throw new ResourceNotFoundException("Position not found.");
                });
    }
}

