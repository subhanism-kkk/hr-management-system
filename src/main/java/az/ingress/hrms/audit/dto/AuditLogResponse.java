package az.ingress.hrms.audit.dto;
import az.ingress.hrms.audit.enums.AuditAction;
import az.ingress.hrms.audit.enums.EntityType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {

    private Integer id;

    private String userName;

    private AuditAction action;

    private EntityType entityType;

    private Integer entityId;

    private String description;

    private LocalDateTime createdAt;

}
