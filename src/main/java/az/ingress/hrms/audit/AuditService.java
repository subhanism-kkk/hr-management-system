package az.ingress.hrms.audit;




import az.ingress.hrms.audit.dto.AuditLogResponse;
import az.ingress.hrms.audit.enums.EntityType;

import java.util.List;

public interface AuditService {

    void logCreate(
            EntityType entityType,
            Integer entityId,
            String description
    );

    void logUpdate(
            EntityType entityType,
            Integer entityId,
            String description
    );

    void logDelete(
            EntityType entityType,
            Integer entityId,
            String description
    );

    void logRestore(
            EntityType entityType,
            Integer entityId,
            String description
    );

    List<AuditLogResponse> getAll();

    AuditLogResponse getById(Integer id);

}
