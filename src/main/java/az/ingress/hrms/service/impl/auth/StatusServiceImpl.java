package az.ingress.hrms.service.impl.auth;

import az.ingress.hrms.entity.lookup.Status;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.log.LogAction;
import az.ingress.hrms.log.lookup.status.StatusLogService;
import az.ingress.hrms.mapper.StatusMapper;
import az.ingress.hrms.repository.StatusRepository;
import az.ingress.hrms.service.auth.StatusService;
import az.ingress.hrms.dto.status.StatusRequest;
import az.ingress.hrms.dto.status.StatusResponse;
import az.ingress.hrms.exception.ResourceAlreadyExistsException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatusServiceImpl implements StatusService {


    private final StatusRepository repository;
    private final StatusMapper mapper;
    private final StatusLogService statusLogService;

    @Override
    @Transactional
    public StatusResponse create(StatusRequest request) {

        if (repository.existsByNameIgnoreCase(request.getName())) {
            throw new ResourceAlreadyExistsException(
                    "Status already exists."
            );
        }

        if (repository.existsByCodeIgnoreCase(request.getCode())) {
            throw new ResourceAlreadyExistsException(
                    "Status already exists."
            );
        }

        Status status = mapper.toEntity(request);

        repository.save(status);

        statusLogService.log(
                status,
                LogAction.POST,
                "admin"
        );

        return mapper.toResponse(status);
    }

    @Override
    @Transactional
    public StatusResponse update(Integer id, StatusRequest request) {

        Status status = fetchStatus(id);

        if (!status.getName().equalsIgnoreCase(request.getName())
                && repository.existsByNameIgnoreCase(request.getName())) {

            throw new ResourceAlreadyExistsException(
                    "Status already exists."
            );
        }

        if (!status.getCode().equalsIgnoreCase(request.getCode())
                && repository.existsByCodeIgnoreCase(request.getCode())) {

            throw new ResourceAlreadyExistsException(
                    "Status already exists."
            );
        }

        statusLogService.log(
                status,
                LogAction.PUT,
                "admin"
        );

        mapper.updateEntity(status, request);

        repository.save(status);

        return mapper.toResponse(status);
    }

    @Override
    public StatusResponse getById(Integer id) {
        Status status = fetchStatus(id);

        return mapper.toResponse(status);
    }

    @Override
    public Page<StatusResponse> getAll(int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("id").ascending());

        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional
    public void softDelete(Integer id) {

        Status status = fetchStatus(id);

        statusLogService.log(
                status,
                LogAction.DELETE,
                "admin"
        );

        status.setIsDeleted(true);
        status.setDeletedAt(LocalDateTime.now());

        status.setDeletedBy("SYSTEM");

        repository.save(status);

    }

    @Override
    @Transactional
    public void restore(Integer id) {

        Status status = repository.findByIdWithDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found."));

        if (!Boolean.TRUE.equals(status.getIsDeleted())) {
            throw new IllegalStateException("Status is not deleted.");
        }

        statusLogService.log(
                status,
                LogAction.PATCH,
                "admin"
        );

        status.setIsDeleted(false);
        status.setDeletedAt(null);
        status.setDeletedBy(null);

        repository.save(status);
    }

    private Status fetchStatus(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "Status info is deleted");
                            });

                    throw new ResourceNotFoundException("Status information not found.");
                });
    }
}
