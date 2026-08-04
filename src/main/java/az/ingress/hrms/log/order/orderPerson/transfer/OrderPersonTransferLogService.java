package az.ingress.hrms.log.order.orderPerson.transfer;

import az.ingress.hrms.entity.order.orderPerson.OrderPersonTransfer;
import az.ingress.hrms.log.CurrentRequestProvider;
import az.ingress.hrms.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderPersonTransferLogService {

    private final OrderPersonTransferLogRepository repository;
    private final CurrentRequestProvider currentRequestProvider;


    @Transactional
    public void log(
            OrderPersonTransfer transfer,
            LogAction action,
            String performedBy) {

        String ipAddress = currentRequestProvider.getIpAddress();

        OrderPersonTransferLog log = OrderPersonTransferLog.builder()
                .mainId(transfer.getId())
                .orderId(
                        transfer.getOrder() != null
                                ? transfer.getOrder().getId()
                                : null
                )
                .personId(
                        transfer.getPerson() != null
                                ? transfer.getPerson().getId()
                                : null
                )
                .oldStructureId(
                        transfer.getOldStructure() != null
                                ? transfer.getOldStructure().getId()
                                : null
                )
                .newStructureId(
                        transfer.getNewStructure() != null
                                ? transfer.getNewStructure().getId()
                                : null
                )
                .oldPositionId(
                        transfer.getOldPosition() != null
                                ? transfer.getOldPosition().getId()
                                : null
                )
                .newPositionId(
                        transfer.getNewPosition() != null
                                ? transfer.getNewPosition().getId()
                                : null
                )
                .effectiveDate(transfer.getEffectiveDate())
                .statusId(
                        transfer.getStatus() != null
                                ? transfer.getStatus().getId()
                                : null
                )
                .createdAt(transfer.getCreatedAt())
                .updatedAt(transfer.getUpdatedAt())
                .isDeleted(transfer.getIsDeleted())
                .deletedAt(transfer.getDeletedAt())
                .deletedBy(transfer.getDeletedBy())
                .actionType(action.name())
                .performedBy(performedBy)
                .ipAddress(ipAddress)
                .loggedAt(LocalDateTime.now())
                .build();

        repository.save(log);
    }
}