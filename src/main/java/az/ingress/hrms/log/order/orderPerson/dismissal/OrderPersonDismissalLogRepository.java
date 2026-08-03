package az.ingress.hrms.log.order.orderPerson.dismissal;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderPersonDismissalLogRepository
        extends JpaRepository<OrderPersonDismissalLog, Integer> {
}