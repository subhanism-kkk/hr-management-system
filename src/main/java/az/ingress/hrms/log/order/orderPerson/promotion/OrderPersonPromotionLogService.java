package az.ingress.hrms.log.order.orderPerson.promotion;

import az.ingress.hrms.entity.order.orderPerson.OrderPersonPromotion;
import az.ingress.hrms.log.CurrentRequestProvider;
import az.ingress.hrms.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderPersonPromotionLogService {

    private final OrderPersonPromotionLogRepository repository;
    private final CurrentRequestProvider currentRequestProvider;


    @Transactional
    public void log(
            OrderPersonPromotion promotion,
            LogAction action,
            String performedBy) {

        String ipAddress = currentRequestProvider.getIpAddress();


        OrderPersonPromotionLog log = OrderPersonPromotionLog.builder()
                .mainId(promotion.getId())
                .orderId(
                        promotion.getOrder() != null
                                ? promotion.getOrder().getId()
                                : null
                )
                .personId(
                        promotion.getPerson() != null
                                ? promotion.getPerson().getId()
                                : null
                )
                .oldPositionId(
                        promotion.getOldPosition() != null
                                ? promotion.getOldPosition().getId()
                                : null
                )
                .newPositionId(
                        promotion.getNewPosition() != null
                                ? promotion.getNewPosition().getId()
                                : null
                )
                .effectiveDate(promotion.getEffectiveDate())
                .statusId(
                        promotion.getStatus() != null
                                ? promotion.getStatus().getId()
                                : null
                )
                .createdAt(promotion.getCreatedAt())
                .updatedAt(promotion.getUpdatedAt())
                .isDeleted(promotion.getIsDeleted())
                .deletedAt(promotion.getDeletedAt())
                .deletedBy(promotion.getDeletedBy())
                .actionType(action.name())
                .performedBy(performedBy)
                .ipAddress(ipAddress)
                .loggedAt(LocalDateTime.now())
                .build();

        repository.save(log);
    }
}