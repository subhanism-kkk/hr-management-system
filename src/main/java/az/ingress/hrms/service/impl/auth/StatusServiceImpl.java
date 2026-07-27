package az.ingress.hrms.service.impl.auth;

import az.ingress.hrms.entity.lookup.Status;
import az.ingress.hrms.mapper.StatusMapper;
import az.ingress.hrms.repository.StatusRepository;
import az.ingress.hrms.service.auth.StatusService;
import az.ingress.hrms.dto.status.StatusRequest;
import az.ingress.hrms.dto.status.StatusResponse;
import az.ingress.hrms.exception.ResourceAlreadyExistsException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {


    private final StatusRepository repository;
    private final StatusMapper mapper;

    @Override
    public StatusResponse create(StatusRequest request) {

        if (repository.existsByNameIgnoreCase(request.getName())) {
            throw new ResourceAlreadyExistsException(
                    "Status already exists."
            );
        }

        Status status = mapper.toEntity(request);

        repository.save(status);

        return mapper.toResponse(status);
    }

    @Override
    public StatusResponse update(Integer id, StatusRequest request) {

        Status status = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Status not found."));

        if (!status.getName().equals(request.getName())
                && repository.existsByNameIgnoreCase(request.getName())) {

            throw new ResourceAlreadyExistsException(
                    "Status already exists."
            );
        }

        mapper.updateEntity(status, request);

        repository.save(status);

        return mapper.toResponse(status);
    }

    @Override
    public StatusResponse getById(Integer id) {
        Status status = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Status not found."));

        return mapper.toResponse(status);
    }

    @Override
    public List<StatusResponse> getAll() {

        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public void softDelete(Integer id) {

        Status status = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Status not found."
                        ));

        status.setIsDeleted(true);
        status.setDeletedAt(LocalDateTime.now());

        status.setDeletedBy("SYSTEM");

        repository.save(status);

    }

    @Override
    public void restore(Integer id) {

        Status status = repository.findByIdWithDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found."));

        status.setIsDeleted(false);
        status.setDeletedAt(null);
        status.setDeletedBy(null);

        repository.save(status);
    }
}
