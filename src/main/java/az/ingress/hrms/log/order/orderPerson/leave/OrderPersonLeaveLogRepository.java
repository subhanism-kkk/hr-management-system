package az.ingress.hrms.log.order.orderPerson.leave;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderPersonLeaveLogRepository
        extends JpaRepository<OrderPersonLeaveLog, Integer> {
}