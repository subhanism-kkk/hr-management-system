package az.ingress.hrms.repository;

import az.ingress.hrms.entity.order.orderPerson.OrderPersonLeave;
import az.ingress.hrms.entity.person.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderPersonLeaveRepository extends JpaRepository<OrderPersonLeave, Integer>, JpaSpecificationExecutor<OrderPersonLeave> {

    Page<OrderPersonLeave> findByPerson(Person person, Pageable pageable);

    @Query(value = "SELECT * FROM order_person_leave WHERE id = :id", nativeQuery = true)
    Optional<OrderPersonLeave> findByIdWithDeleted(@Param("id") Integer id);

    @Query("""
            SELECT COUNT(l) > 0 FROM OrderPersonLeave l
            WHERE l.person.id = :personId
              AND l.status.code = 'ACTIVE'
              AND l.isDeleted = false
              AND l.startDate <= :endDate
              AND l.endDate >= :startDate
            """)
    boolean existsOverlappingLeave(
            @Param("personId") Integer personId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            SELECT COUNT(l) > 0 FROM OrderPersonLeave l
            WHERE l.person.id = :personId
              AND l.id != :excludeId
              AND l.status.code = 'ACTIVE'
              AND l.isDeleted = false
              AND l.startDate <= :endDate
              AND l.endDate >= :startDate
            """)
    boolean existsOverlappingLeaveExcludingId(
            @Param("personId") Integer personId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeId") Integer excludeId
    );
}