package az.ingress.hrms.repository.organization;

import az.ingress.hrms.entity.organization.Position;
import az.ingress.hrms.entity.organization.StaffingPlan;
import az.ingress.hrms.entity.organization.Structure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffingPlanRepository extends JpaRepository<StaffingPlan, Integer>, JpaSpecificationExecutor<StaffingPlan> {

    @Query(value = "SELECT * FROM Staffing_Plan WHERE id = :id", nativeQuery = true)
    Optional<StaffingPlan> findByIdWithDeleted(Integer id);

    boolean existsByStructureAndPosition(Structure structure, Position position);

    Optional<StaffingPlan> findByStructureIdAndPositionId(
            Integer structureId,
            Integer positionId
    );

      List<StaffingPlan> findAllByOrderId(Integer orderId);

    @Query(value = "SELECT * FROM Staffing_Plan WHERE order_id = :orderId", nativeQuery = true)
    List<StaffingPlan> findAllByOrderIdWithDeleted(Integer orderId);

    List<StaffingPlan> findByOrderIdAndIsDeletedFalse(Integer orderId);

    Optional<StaffingPlan> findByOrderIdAndPositionId(Integer orderId, Integer positionId);
}