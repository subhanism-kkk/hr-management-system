package az.ingress.hrms.repository.order;

import az.ingress.hrms.entity.order.Order;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonAppointment;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonBonus;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonTransfer;
import az.ingress.hrms.entity.organization.StaffingPlan;
import az.ingress.hrms.entity.person.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderPersonAppointmentRepository extends JpaRepository<OrderPersonAppointment, Integer>, JpaSpecificationExecutor<OrderPersonAppointment> {

    @Query(value = " SELECT * FROM Order_Person_Appointment WHERE id = :id", nativeQuery = true)
    Optional<OrderPersonAppointment> findByIdWithDeleted(Integer id);

    boolean existsByPersonIdAndIsClosedFalse(Integer id);

    long countByStaffingPlanIdAndIsClosedFalse(Integer id);

    @Query("""
                SELECT a FROM OrderPersonAppointment a 
                WHERE a.person.id = :personId 
                  AND a.startDate <= :asOfDate 
                  AND (a.endDate IS NULL OR a.endDate >= :asOfDate) 
                  AND (a.isDeleted = false OR a.isDeleted IS NULL)
            """)
    Optional<OrderPersonAppointment> findActiveAppointment(
            @Param("personId") Long personId,
            @Param("asOfDate") LocalDate asOfDate
    );

    Optional<OrderPersonAppointment> findByPersonIdAndIsClosedFalse(
            Integer personId
    );

    @Query(value = "SELECT * FROM order_person_appointment opa WHERE opa.order_id = :orderId", nativeQuery = true)
    List<OrderPersonAppointment> findAllByOrderIdWithDeleted(@Param("orderId") Integer orderId);

    List<OrderPersonAppointment> findAllByOrderId(Integer orderId);

    Optional<OrderPersonAppointment> findByOrderIdAndPersonId(
            Integer orderId,
            Integer personId
    );

    Optional<OrderPersonAppointment> findByDismissalOrderIdAndPersonId(
            Integer dismissalOrderId,
            Integer personId
    );

    List<OrderPersonAppointment> findByOrderIdAndIsDeletedFalse(Integer orderId);


}
