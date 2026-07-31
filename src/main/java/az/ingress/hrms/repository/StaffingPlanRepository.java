package az.ingress.hrms.repository;

import az.ingress.hrms.entity.organization.Position;
import az.ingress.hrms.entity.organization.StaffingPlan;
import az.ingress.hrms.entity.organization.Structure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffingPlanRepository extends JpaRepository<StaffingPlan,Integer> {

    @Query(value = " SELECT * FROM Staffing_Plan WHERE id = :id", nativeQuery = true)
    Optional<StaffingPlan> findByIdWithDeleted(Integer id);

    List<StaffingPlan> findByStructure(Structure structure);

    List<StaffingPlan> findByPosition(Position position);

    boolean existsByStructureAndPosition(Structure structure, Position position);

    Optional<StaffingPlan> findByStructureIdAndPositionId(
            Integer structureId,
            Integer positionId
    );

}
