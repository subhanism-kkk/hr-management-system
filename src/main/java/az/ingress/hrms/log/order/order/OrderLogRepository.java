package az.ingress.hrms.log.order.order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLogRepository extends JpaRepository<OrderLog, Integer> {
}