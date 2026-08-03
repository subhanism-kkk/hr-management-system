package az.ingress.hrms.log.lookup.status;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusLogRepository extends JpaRepository<StatusLog, Integer> {
}