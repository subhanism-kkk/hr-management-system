package az.ingress.hrms.repository;

import az.ingress.hrms.entity.lookup.BonusType;
import az.ingress.hrms.entity.lookup.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderTypeRepository extends JpaRepository<OrderType, Integer>, JpaSpecificationExecutor<OrderType> {

    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT b FROM OrderType b WHERE b.isDeleted = false AND b.status.code = 'ACTIVE' ORDER BY b.name ASC")
    List<OrderType> findAllActive();

    // for restore method
    @Query(value = "SELECT * FROM Order_Types WHERE id = :id", nativeQuery = true)
    Optional<OrderType> findByIdWithDeleted(@Param("id") Integer id);
}