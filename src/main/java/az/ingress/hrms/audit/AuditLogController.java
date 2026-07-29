package az.ingress.hrms.audit;

import az.ingress.hrms.audit.dto.AuditLogResponse;
import az.ingress.hrms.audit.enums.AuditAction;
import az.ingress.hrms.audit.enums.EntityType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditService auditService;

    @GetMapping
    public List<AuditLogResponse> getAll() {
        return auditService.getAll();
    }

    @GetMapping("/{id}")
    public AuditLogResponse getById(
            @PathVariable Integer id
    ) {
        return auditService.getById(id);
    }

    @GetMapping("/entity/{entityType}")
    public List<AuditLogResponse> getByEntity(
            @PathVariable EntityType entityType
    ) {

        return auditService.getByEntity(entityType);

    }

    @GetMapping("/action/{action}")
    public List<AuditLogResponse> getByAction(
            @PathVariable AuditAction action
    ) {

        return auditService.getByAction(action);

    }

    @GetMapping("/user/{username}")
    public List<AuditLogResponse> getByUser(
            @PathVariable String username
    ) {

        return auditService.getByUser(username);

    }

}