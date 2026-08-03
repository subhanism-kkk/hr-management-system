package az.ingress.hrms.log.order.orderType;

import az.ingress.hrms.entity.order.OrderType;
import az.ingress.hrms.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderTypeLogService {

    private final OrderTypeLogRepository repository;

    @Transactional
    public void log(
            OrderType orderType,
            LogAction action,
            String performedBy
    ) {

        OrderTypeLog log = OrderTypeLog.builder()
                .mainId(orderType.getId())
                .name(orderType.getName())
                .description(orderType.getDescription())
                .code(orderType.getCode())
                .createdAt(orderType.getCreatedAt())
                .updatedAt(orderType.getUpdatedAt())
                .isDeleted(orderType.getIsDeleted())
                .deletedAt(orderType.getDeletedAt())
                .deletedBy(orderType.getDeletedBy())
                .actionType(action.name())
                .performedBy(performedBy)
                .loggedAt(LocalDateTime.now())
                .build();

        repository.save(log);
    }
}