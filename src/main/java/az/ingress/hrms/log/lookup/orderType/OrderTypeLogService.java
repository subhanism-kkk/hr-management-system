package az.ingress.hrms.log.lookup.orderType;

import az.ingress.hrms.entity.lookup.OrderType;
import az.ingress.hrms.log.CurrentRequestProvider;
import az.ingress.hrms.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderTypeLogService {

    private final OrderTypeLogRepository repository;
    private final CurrentRequestProvider currentRequestProvider;

    @Transactional
    public void log(
            OrderType orderType,
            LogAction action,
            String performedBy) {

        String ipAddress = currentRequestProvider.getIpAddress();


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
                .ipAddress(ipAddress)
                .loggedAt(LocalDateTime.now())
                .build();

        repository.save(log);
    }
}