package az.ingress.hrms.log.order.orderPerson.salary;


import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderPersonSalaryLogRepository
        extends JpaRepository<OrderPersonSalaryLog, Integer> {
}