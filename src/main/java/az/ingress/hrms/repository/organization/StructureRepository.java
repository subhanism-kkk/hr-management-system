package az.ingress.hrms.repository.organization;

import az.ingress.hrms.entity.organization.StaffingPlan;
import az.ingress.hrms.entity.organization.Structure;
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
public interface StructureRepository extends JpaRepository<Structure, Integer>, JpaSpecificationExecutor<Structure> {

    @Query(value = "SELECT * FROM Structure WHERE id = :id", nativeQuery = true)
    Optional<Structure> findByIdWithDeleted(@Param("id") Integer id);

    boolean existsByNameIgnoreCase(String name);

    Optional<Structure> findByOrderId(Integer orderId);

    boolean existsByParentStructure(Structure parent);

    List<Structure> findAllByOrderId(Integer orderId);

    @Query(value = "SELECT * FROM Structure WHERE order_id = :orderId", nativeQuery = true)
    Optional<Structure> findByOrderIdWithDeleted(@Param("orderId") Integer orderId);
}