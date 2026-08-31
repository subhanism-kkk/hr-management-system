package az.ingress.hrms.repository.order;

import az.ingress.hrms.entity.order.orderPerson.OrderPersonBonus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderPersonBonusRepository
        extends JpaRepository<OrderPersonBonus, Long>,
        JpaSpecificationExecutor<OrderPersonBonus> {

    @Query(value = "SELECT * FROM order_person_bonuses WHERE id = :id", nativeQuery = true)
    Optional<OrderPersonBonus> findByIdWithDeleted(@Param("id") Long id);

    Optional<OrderPersonBonus> findByOrderIdAndPersonId(Integer orderId, Integer personId);

    List<OrderPersonBonus> findAllByOrderId(Integer orderId);

    @Query(value = "SELECT * FROM order_person_bonus WHERE order_id = :orderId", nativeQuery = true)
    List<OrderPersonBonus> findAllByOrderIdWithDeleted(@Param("orderId") Integer orderId);

    @Query("SELECT b FROM OrderPersonBonus b WHERE b.person.id = :personId " +
            "AND b.status.code = 'ACTIVE' " +
            "AND b.startDate <= :asOfDate" +
            " AND (b.endDate IS NULL OR b.endDate >= :asOfDate) " +
            "AND (b.isDeleted = false OR b.isDeleted IS NULL)")
    List<OrderPersonBonus> findActiveBonuses(@Param("personId") Long personId, @Param("asOfDate") LocalDate asOfDate);

    List<OrderPersonBonus> findByOrderIdAndIsDeletedFalse(Integer orderId);}


