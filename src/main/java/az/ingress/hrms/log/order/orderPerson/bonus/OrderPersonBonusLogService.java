package az.ingress.hrms.log.order.orderPerson.bonus;

import az.ingress.hrms.entity.order.orderPerson.OrderPersonBonus;
import az.ingress.hrms.log.CurrentRequestProvider;
import az.ingress.hrms.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderPersonBonusLogService {

    private final OrderPersonBonusLogRepository repository;
    private final CurrentRequestProvider currentRequestProvider;

    @Transactional
    public void log(
            OrderPersonBonus bonus,
            LogAction action,
            String performedBy) {

        String ipAddress = currentRequestProvider.getIpAddress();

        OrderPersonBonusLog log = OrderPersonBonusLog.builder()
                .mainId(bonus.getId())
                .orderId(bonus.getOrder() != null ? bonus.getOrder().getId() : null)
                .personId(bonus.getPerson() != null ? bonus.getPerson().getId() : null)
                .bonusTypeId(bonus.getBonusType() != null ? bonus.getBonusType().getId() : null)
                .calculationType(bonus.getCalculationType())
                .amount(bonus.getAmount())
                .startDate(bonus.getStartDate())
                .endDate(bonus.getEndDate())
                .reason(bonus.getReason())
                .createdAt(bonus.getCreatedAt())
                .updatedAt(bonus.getUpdatedAt())
                .isDeleted(bonus.getIsDeleted())
                .deletedAt(bonus.getDeletedAt())
                .deletedBy(bonus.getDeletedBy())
                .actionType(action.name())
                .performedBy(performedBy)
                .loggedAt(LocalDateTime.now())
                .ipAddress(ipAddress)
                .build();

        repository.save(log);
    }
}