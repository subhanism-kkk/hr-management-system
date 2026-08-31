package az.ingress.hrms.repository.order;

import az.ingress.hrms.entity.order.orderPerson.OrderPersonSalary;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonTransfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderPersonSalaryRepository extends JpaRepository<OrderPersonSalary, Integer>, JpaSpecificationExecutor<OrderPersonSalary> {

    @Query(value = "SELECT * FROM order_person_salary WHERE id = :id", nativeQuery = true)
    Optional<OrderPersonSalary> findByIdWithDeleted(@Param("id") Integer id);

    Page<OrderPersonSalary> findByStaffingPlanId(Integer staffingPlanId, Pageable pageable);

    Optional<OrderPersonSalary> findByOrderIdAndStaffingPlanId(Integer orderId, Integer staffingPlanId);

    List<OrderPersonSalary> findAllByOrderId(Integer orderId);

    @Query(value = "SELECT * FROM order_person_salary WHERE order_id = :orderId", nativeQuery = true)
    List<OrderPersonSalary> findAllByOrderIdWithDeleted(@Param("orderId") Integer orderId);

    List<OrderPersonSalary> findByOrderIdAndIsDeletedFalse(Integer orderId);

}