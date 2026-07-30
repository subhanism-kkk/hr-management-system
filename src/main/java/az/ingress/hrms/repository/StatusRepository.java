package az.ingress.hrms.repository;

import az.ingress.hrms.entity.lookup.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {

    Optional<Status> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);


    @Query(value = "SELECT * FROM Status WHERE id = :id", nativeQuery = true)
    Optional<Status> findByIdWithDeleted(@Param("id") Integer id);

    Optional<Status> findByCode(String code);

    boolean existsByCodeIgnoreCase(String code);
}