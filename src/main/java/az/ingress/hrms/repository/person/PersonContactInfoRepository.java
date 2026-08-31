package az.ingress.hrms.repository.person;

import az.ingress.hrms.entity.lookup.ContactType;
import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.entity.person.PersonContactInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PersonContactInfoRepository
        extends JpaRepository<PersonContactInfo, Integer>, JpaSpecificationExecutor<PersonContactInfo> {

    List<PersonContactInfo> findByPerson(Person person);

    Page<PersonContactInfo> findByPersonId(Integer personId, Pageable pageable);

    Optional<PersonContactInfo> findByPersonAndIsPrimaryTrue(Person person);

    boolean existsByPersonAndContactTypeAndContactValueIgnoreCase(
            Person person,
            ContactType contactType,
            String contactValue
    );

    @Query(value = " SELECT * FROM Person_Contact_Info WHERE id = :id", nativeQuery = true)
    Optional<PersonContactInfo> findByIdWithDeleted(Integer id);

}