package az.ingress.hrms.repository;

import az.ingress.hrms.entity.order.orderPerson.OrderPersonDismissal;
import az.ingress.hrms.entity.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderPersonDismissalRepository extends JpaRepository<OrderPersonDismissal, Integer> {

    List<OrderPersonDismissal> findByPerson(Person person);

    @Query(value = "SELECT * FROM order_person_dismissal WHERE id = :id", nativeQuery = true)
    Optional<OrderPersonDismissal> findByIdWithDeleted(@Param("id") Integer id);

    boolean existsByPersonIdAndStatusCodeAndIsDeletedFalse(Integer personId, String statusCode);
}