package az.ingress.hrms.repository;

import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.entity.person.PersonContactInfo;
import az.ingress.hrms.entity.person.PersonPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonPhotoRepository extends JpaRepository<PersonPhoto,Integer> {

    @Query(value = " SELECT * FROM Person_Photo WHERE id = :id", nativeQuery = true)
    Optional<PersonPhoto> findByIdWithDeleted(Integer id);

    List<PersonPhoto> findByPerson(Person person);

    List<PersonPhoto> findByPersonId(Integer personId);

    Optional<PersonPhoto> findByPersonAndIsMainTrue(Person person);

    boolean existsByPersonAndFilePath(Person person, String filePath);
}
