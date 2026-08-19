package az.ingress.hrms.repository;

import az.ingress.hrms.entity.order.orderPerson.OrderPersonTransfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderPersonTransferRepository extends JpaRepository<OrderPersonTransfer, Integer>, JpaSpecificationExecutor<OrderPersonTransfer> {


    @Query(value = " SELECT * FROM order_person_transfer WHERE id = :id", nativeQuery = true)
    Optional<OrderPersonTransfer> findByIdWithDeleted(Integer id);

    Page<OrderPersonTransfer> findByPersonId(Integer personId, Pageable pageable);


}
