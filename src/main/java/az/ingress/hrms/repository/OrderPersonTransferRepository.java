package az.ingress.hrms.repository;

import az.ingress.hrms.entity.order.orderPerson.OrderPersonTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderPersonTransferRepository extends JpaRepository<OrderPersonTransfer, Integer> {



    @Query(value = "SELECT * FROM order_person_transfer WHERE id = :id AND is_deleted = true", nativeQuery = true)
    Optional<OrderPersonTransfer> findDeletedById(@Param("id") Integer id);

    List<OrderPersonTransfer> findByPersonId(Integer personId);


}
