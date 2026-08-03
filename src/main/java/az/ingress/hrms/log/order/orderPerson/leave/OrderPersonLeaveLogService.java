package az.ingress.hrms.log.order.orderPerson.leave;

import az.ingress.hrms.entity.order.orderPerson.OrderPersonLeave;
import az.ingress.hrms.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderPersonLeaveLogService {

    private final OrderPersonLeaveLogRepository repository;

    @Transactional
    public void log(
            OrderPersonLeave leave,
            LogAction action,
            String performedBy
    ) {

        OrderPersonLeaveLog log = OrderPersonLeaveLog.builder()
                .mainId(leave.getId())
                .orderId(
                        leave.getOrder() != null
                                ? leave.getOrder().getId()
                                : null
                )
                .personId(
                        leave.getPerson() != null
                                ? leave.getPerson().getId()
                                : null
                )
                .leaveTypeId(
                        leave.getLeaveType() != null
                                ? leave.getLeaveType().getId()
                                : null
                )
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .reason(leave.getReason())
                .statusId(
                        leave.getStatus() != null
                                ? leave.getStatus().getId()
                                : null
                )
                .createdAt(leave.getCreatedAt())
                .updatedAt(leave.getUpdatedAt())
                .isDeleted(leave.getIsDeleted())
                .deletedAt(leave.getDeletedAt())
                .deletedBy(leave.getDeletedBy())
                .actionType(action.name())
                .performedBy(performedBy)
                .loggedAt(LocalDateTime.now())
                .build();

        repository.save(log);
    }
}