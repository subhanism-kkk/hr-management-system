package az.ingress.hrms.repository;

import az.ingress.hrms.entity.lookup.BonusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BonusTypeRepository extends JpaRepository<BonusType, Integer>, JpaSpecificationExecutor<BonusType> {

    Optional<BonusType> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT b FROM BonusType b WHERE b.isDeleted = false AND b.status.code = 'ACTIVE' ORDER BY b.name ASC")
    List<BonusType> findAllActive();

    // for restore method
    @Query(value = "SELECT * FROM Bonus_Type WHERE id = :id", nativeQuery = true)
    Optional<BonusType> findByIdWithDeleted(@Param("id") Integer id);
}