package az.ingress.hrms.repository;

import az.ingress.hrms.entity.lookup.BonusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BonusTypeRepository
        extends JpaRepository<BonusType, Long>,
        JpaSpecificationExecutor<BonusType> {

    @Query(value = "SELECT * FROM bonus_types WHERE id = :id", nativeQuery = true)
    Optional<BonusType> findByIdWithDeleted(@Param("id") Long id);
}