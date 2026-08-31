package az.ingress.hrms.repository.person;

import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.entity.person.PersonPersonalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonPersonalInfoRepository extends JpaRepository<PersonPersonalInfo,Integer>, JpaSpecificationExecutor<PersonPersonalInfo> {

    Optional<PersonPersonalInfo> findByPerson(Person person);

    Optional<PersonPersonalInfo> findByFinCode(String finCode);

    boolean existsByPerson(Person person);

    boolean existsByFinCode(String finCode);

    // for restore method
    @Query(value = "SELECT * FROM Order_Types WHERE id = :id", nativeQuery = true)
    Optional<PersonPersonalInfo> findByIdWithDeleted(@Param("id") Integer id);
}
