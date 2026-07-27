package az.ingress.hrms.audit;

import az.ingress.hrms.audit.dto.AuditLogResponse;
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

}