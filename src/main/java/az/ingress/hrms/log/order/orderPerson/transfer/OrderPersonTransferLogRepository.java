package az.ingress.hrms.log.order.orderPerson.transfer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderPersonTransferLogRepository
        extends JpaRepository<OrderPersonTransferLog, Integer> {
}