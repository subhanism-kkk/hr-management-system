package az.ingress.hrms.repository;

import az.ingress.hrms.entity.order.orderPerson.OrderPersonPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderPersonPromotionRepository extends JpaRepository<OrderPersonPromotion, Integer> {

    List<OrderPersonPromotion> findByPersonId(Integer personId);

    @Query(value = "SELECT * FROM order_person_promotion WHERE id = :id ", nativeQuery = true)
    Optional<OrderPersonPromotion> findByIdWithDeleted(Integer id);
}