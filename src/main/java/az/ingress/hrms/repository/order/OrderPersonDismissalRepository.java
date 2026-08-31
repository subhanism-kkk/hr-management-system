package az.ingress.hrms.repository.order;

import az.ingress.hrms.entity.order.orderPerson.OrderPersonDismissal;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonTransfer;
import az.ingress.hrms.entity.person.Person;
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
public interface OrderPersonDismissalRepository extends JpaRepository<OrderPersonDismissal, Integer>, JpaSpecificationExecutor<OrderPersonDismissal> {

    Page<OrderPersonDismissal> findByPerson(Person person, Pageable pageable);

    @Query(value = "SELECT * FROM order_person_dismissal WHERE id = :id", nativeQuery = true)
    Optional<OrderPersonDismissal> findByIdWithDeleted(@Param("id") Integer id);

    boolean existsByPersonIdAndStatusCodeAndIsDeletedFalse(Integer personId, String statusCode);

    Optional<OrderPersonDismissal> findByOrderIdAndPersonId(Integer orderId, Integer personId);

    List<OrderPersonDismissal> findAllByOrderId(Integer orderId);

    @Query(value = "SELECT * FROM order_person_dismissal WHERE order_id = :orderId", nativeQuery = true)
    List<OrderPersonDismissal> findAllByOrderIdWithDeleted(@Param("orderId") Integer orderId);

    List<OrderPersonDismissal> findByOrderIdAndIsDeletedFalse(Integer orderId);

}