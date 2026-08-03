package az.ingress.hrms.log.order.orderPerson.dismissal;

import az.ingress.hrms.entity.order.orderPerson.OrderPersonDismissal;
import az.ingress.hrms.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderPersonDismissalLogService {

    private final OrderPersonDismissalLogRepository repository;

    @Transactional
    public void log(
            OrderPersonDismissal dismissal,
            LogAction action,
            String performedBy
    ) {

        OrderPersonDismissalLog log = OrderPersonDismissalLog.builder()
                .mainId(dismissal.getId())
                .orderId(
                        dismissal.getOrder() != null
                                ? dismissal.getOrder().getId()
                                : null
                )
                .personId(
                        dismissal.getPerson() != null
                                ? dismissal.getPerson().getId()
                                : null
                )
                .dismissalDate(dismissal.getDismissalDate())
                .description(dismissal.getDescription())
                .statusId(
                        dismissal.getStatus() != null
                                ? dismissal.getStatus().getId()
                                : null
                )
                .createdAt(dismissal.getCreatedAt())
                .updatedAt(dismissal.getUpdatedAt())
                .isDeleted(dismissal.getIsDeleted())
                .deletedAt(dismissal.getDeletedAt())
                .deletedBy(dismissal.getDeletedBy())
                .actionType(action.name())
                .performedBy(performedBy)
                .loggedAt(LocalDateTime.now())
                .build();

        repository.save(log);
    }
}