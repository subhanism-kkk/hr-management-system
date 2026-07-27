package az.ingress.hrms.repository;

import az.ingress.hrms.entity.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

    Optional<Person> findByFirstNameAndLastNameIgnoreCase(String firstName, String lastName);

    boolean existsByFirstNameAndLastNameIgnoreCase(String firstName, String lastName);

    // Native query bypasses the @SQLRestriction to allow restoring deleted persons
    @Query(value = "SELECT * FROM Persons WHERE id = :id", nativeQuery = true)
    Optional<Person> findByIdWithDeleted(@Param("id") Integer id);
}