package az.ingress.hrms.repository;

import az.ingress.hrms.entity.lookup.ContactType;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactTypeRepository extends JpaRepository<ContactType, Integer>, JpaSpecificationExecutor<ContactType> {

    Optional<ContactType> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    @Query(value = "SELECT * FROM contact_type WHERE id = :id ", nativeQuery = true)
    Optional<ContactType> findByIdWithDeleted( Integer id);

}
