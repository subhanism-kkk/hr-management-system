package az.ingress.hrms.audit;

import az.ingress.hrms.audit.enums.AuditAction;
import az.ingress.hrms.audit.enums.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository
        extends JpaRepository<AuditLog,Integer> {

    List<AuditLog> findByEntityType(EntityType entityType);

    List<AuditLog> findByEntityId(Integer entityId);

    List<AuditLog> findByAction(AuditAction action);

    List<AuditLog> findByUserName(String userName);

}
