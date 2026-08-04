package az.ingress.hrms.log.order.orderPerson.salary;

import az.ingress.hrms.entity.order.orderPerson.OrderPersonSalary;
import az.ingress.hrms.log.CurrentRequestProvider;
import az.ingress.hrms.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderPersonSalaryLogService {

    private final OrderPersonSalaryLogRepository repository;
    private final CurrentRequestProvider currentRequestProvider;

    @Transactional
    public void log(
            OrderPersonSalary salary,
            LogAction action,
            String performedBy) {

        String ipAddress = currentRequestProvider.getIpAddress();


        OrderPersonSalaryLog log = OrderPersonSalaryLog.builder()
                .mainId(salary.getId())
                .orderId(
                        salary.getOrder() != null
                                ? salary.getOrder().getId()
                                : null
                )
                .staffingPlanId(
                        salary.getStaffingPlan() != null
                                ? salary.getStaffingPlan().getId()
                                : null
                )
                .oldSalary(salary.getOldSalary())
                .newSalary(salary.getNewSalary())
                .effectiveDate(salary.getEffectiveDate())
                .statusId(
                        salary.getStatus() != null
                                ? salary.getStatus().getId()
                                : null
                )
                .createdAt(salary.getCreatedAt())
                .updatedAt(salary.getUpdatedAt())
                .isDeleted(salary.getIsDeleted())
                .deletedAt(salary.getDeletedAt())
                .deletedBy(salary.getDeletedBy())
                .actionType(action.name())
                .performedBy(performedBy)
                .loggedAt(LocalDateTime.now())
                .ipAddress(ipAddress)
                .build();

        repository.save(log);
    }
}
