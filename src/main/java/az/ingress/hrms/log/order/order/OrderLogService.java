package az.ingress.hrms.log.order.order;

import az.ingress.hrms.entity.order.Order;
import az.ingress.hrms.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderLogService {

    private final OrderLogRepository repository;

    @Transactional
    public void log(
            Order order,
            LogAction action,
            String performedBy
    ) {

        OrderLog log = OrderLog.builder()
                .mainId(order.getId())
                .orderTypeId(
                        order.getOrderType() != null
                                ? order.getOrderType().getId()
                                : null
                )
                .orderNumber(order.getOrderNumber())
                .orderDate(order.getOrderDate())
                .statusId(
                        order.getStatus() != null
                                ? order.getStatus().getId()
                                : null
                )
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .isDeleted(order.getIsDeleted())
                .deletedAt(order.getDeletedAt())
                .deletedBy(order.getDeletedBy())
                .actionType(action.name())
                .performedBy(performedBy)
                .loggedAt(LocalDateTime.now())
                .build();

        repository.save(log);
    }
}