package az.ingress.hrms.repository;

import az.ingress.hrms.entity.lookup.ContactType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactTypeRepository extends JpaRepository<ContactType, Integer> {

    Optional<ContactType> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    @Query(value = "SELECT * FROM Contact_Types WHERE id = :id", nativeQuery = true)
    Optional<ContactType> findByIdWithDeleted(@Param("id") Integer id);

}
