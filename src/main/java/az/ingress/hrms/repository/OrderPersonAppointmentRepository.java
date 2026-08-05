package az.ingress.hrms.repository;

import az.ingress.hrms.entity.order.Order;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonAppointment;
import az.ingress.hrms.entity.organization.StaffingPlan;
import az.ingress.hrms.entity.person.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderPersonAppointmentRepository extends JpaRepository<OrderPersonAppointment, Integer> {
    Page<OrderPersonAppointment> findByPerson(Person person, Pageable pageable);

    List<OrderPersonAppointment> findByStaffingPlan(
            StaffingPlan staffingPlan
    );

    List<OrderPersonAppointment> findByOrder(Order order);

    Optional<OrderPersonAppointment>
    findByPersonAndIsClosedFalse(Person person);

    @Query(value = " SELECT * FROM Order_Person_Appointment WHERE id = :id", nativeQuery = true)
    Optional<OrderPersonAppointment> findByIdWithDeleted(Integer id);

    boolean existsByPersonIdAndIsClosedFalse(Integer id);

    long countByStaffingPlanIdAndIsClosedFalse(Integer id);


    Optional<OrderPersonAppointment> findByPersonIdAndIsClosedFalse(
            Integer personId
    );
}
