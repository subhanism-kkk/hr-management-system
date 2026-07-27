package az.ingress.hrms.repository;

import az.ingress.hrms.entity.order.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderTypeRepository extends JpaRepository<OrderType, Integer> {

    Optional<OrderType> findByNameIgnoreCase(String name);

    Optional<OrderType> findByCodeIgnoreCase(String code);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByCodeIgnoreCase(String code);

    // for restore method
    @Query(value = "SELECT * FROM Order_Types WHERE id = :id", nativeQuery = true)
    Optional<OrderType> findByIdWithDeleted(@Param("id") Integer id);
}