package az.ingress.hrms.repository;

import az.ingress.hrms.entity.order.orderPerson.OrderPersonSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderPersonSalaryRepository extends JpaRepository<OrderPersonSalary, Integer> {

    List<OrderPersonSalary> findByPersonId(Integer personId);

    @Query(value = "SELECT * FROM order_person_salary WHERE id = :id AND is_deleted = true", nativeQuery = true)
    Optional<OrderPersonSalary> findDeletedById(@Param("id") Integer id);
}