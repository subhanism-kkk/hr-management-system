package az.ingress.hrms.repository;

import az.ingress.hrms.entity.organization.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position,Integer> {
    Optional<Position> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);

    // Native query bypasses the @SQLRestriction to allow restoring deleted
    @Query(value = "SELECT * FROM Position WHERE id = :id", nativeQuery = true)
    Optional<Position> findByIdWithDeleted(@Param("id") Integer id);
}
