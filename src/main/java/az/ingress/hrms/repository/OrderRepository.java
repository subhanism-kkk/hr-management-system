package az.ingress.hrms.repository;

import az.ingress.hrms.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository
        extends JpaRepository<Order, Integer> {

    @Query(
            value = "SELECT NEXT VALUE FOR Order_Number_Seq",
            nativeQuery = true
    )
    Long getNextOrderSequence();

    boolean existsByOrderNumber(String orderNumber);

    Optional<Order> findByOrderNumber(String orderNumber);

    @Query(value = "SELECT * FROM Orders WHERE id = :id", nativeQuery = true)
    Optional<Order> findByIdWithDeleted(@Param("id") Integer id);

}