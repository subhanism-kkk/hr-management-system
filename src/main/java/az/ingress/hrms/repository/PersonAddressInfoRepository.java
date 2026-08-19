package az.ingress.hrms.repository;

import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.entity.person.PersonAddressInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PersonAddressInfoRepository
        extends JpaRepository<PersonAddressInfo, Integer>, JpaSpecificationExecutor<PersonAddressInfo> {

    @Query(value = "SELECT * FROM Person_Address_Info WHERE id = :id", nativeQuery = true)
    Optional<PersonAddressInfo> findByIdWithDeleted(@Param("id") Integer id);

    Page<PersonAddressInfo> findByPerson(Person person, Pageable pageable);

    boolean existsByPersonAndAddressIgnoreCase(
            Person person,
            String address
    );
}