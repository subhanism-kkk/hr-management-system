package az.ingress.hrms.log.order.orderPerson.appointment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderPersonAppointmentLogRepository
        extends JpaRepository<OrderPersonAppointmentLog, Integer> {
}