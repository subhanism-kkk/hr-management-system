package az.ingress.hrms.repository;

import az.ingress.hrms.entity.order.orderPerson.OrderPersonSalary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface OrderPersonSalaryRepository extends JpaRepository<OrderPersonSalary, Integer>, JpaSpecificationExecutor<OrderPersonSalary> {

     @Query(value = " SELECT * FROM order_person_salary WHERE id = :id", nativeQuery = true)
        Optional<OrderPersonSalary> findByIdWithDeleted(Integer id);

    Page<OrderPersonSalary> findByStaffingPlanId(Integer staffingPlanId, Pageable pageable);
}