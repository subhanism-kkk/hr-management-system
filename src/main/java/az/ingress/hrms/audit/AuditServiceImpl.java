package az.ingress.hrms.audit;

import az.ingress.hrms.audit.dto.AuditLogResponse;
import az.ingress.hrms.audit.enums.AuditAction;
import az.ingress.hrms.audit.enums.EntityType;
import az.ingress.hrms.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository repository;

    private final AuditLogMapper mapper;

    @Override
    public void logCreate(
            EntityType entityType,
            Integer entityId,
            String description
    ) {

        saveLog(
                AuditAction.CREATE,
                entityType,
                entityId,
                description
        );

    }

    @Override
    public void logUpdate(
            EntityType entityType,
            Integer entityId,
            String description
    ) {

        saveLog(
                AuditAction.UPDATE,
                entityType,
                entityId,
                description
        );

    }

    @Override
    public void logDelete(
            EntityType entityType,
            Integer entityId,
            String description
    ) {

        saveLog(
                AuditAction.DELETE,
                entityType,
                entityId,
                description
        );

    }

    @Override
    public void logRestore(
            EntityType entityType,
            Integer entityId,
            String description
    ) {

        saveLog(
                AuditAction.RESTORE,
                entityType,
                entityId,
                description
        );

    }

    private void saveLog(
            AuditAction action,
            EntityType entityType,
            Integer entityId,
            String description
    ) {

        AuditLog log = AuditLog.builder()

                .action(action)

                .entityType(entityType)

                .entityId(entityId)

                .description(description)

                // later replace SYSTEM
                .userName("SYSTEM")

                .build();

        repository.save(log);

    }

    @Override
    public List<AuditLogResponse> getAll() {

        return repository.findAll()

                .stream()

                .map(mapper::toResponse)

                .toList();

    }

    @Override
    public AuditLogResponse getById(Integer id) {

        AuditLog log = repository.findById(id)

                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Audit log not found."
                        ));

        return mapper.toResponse(log);

    }

}