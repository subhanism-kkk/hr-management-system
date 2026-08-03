package az.ingress.hrms.log.order.orderPerson;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderPersonTransferLogRepository
        extends JpaRepository<OrderPersonTransferLog, Integer> {
}