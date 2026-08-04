package az.ingress.hrms.repository;

import az.ingress.hrms.entity.order.orderPerson.OrderPersonSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderPersonSalaryRepository extends JpaRepository<OrderPersonSalary, Integer> {

     @Query(value = " SELECT * FROM order_person_salary WHERE id = :id", nativeQuery = true)
        Optional<OrderPersonSalary> findByIdWithDeleted(Integer id);

    List<OrderPersonSalary> findByStaffingPlanId(Integer staffingPlanId);
}