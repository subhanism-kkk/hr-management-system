package az.ingress.hrms.repository;

import az.ingress.hrms.entity.lookup.ContactType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactTypeRepository extends JpaRepository<ContactType, Integer>, JpaSpecificationExecutor<ContactType> {

    Optional<ContactType> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT c FROM ContactType c WHERE c.isDeleted = false AND c.status.code = 'ACTIVE' ORDER BY c.name ASC")
    List<ContactType> findAllActive();

    @Query(value = "SELECT * FROM contact_type WHERE id = :id", nativeQuery = true)
    Optional<ContactType> findByIdWithDeleted(@Param("id") Integer id);
}