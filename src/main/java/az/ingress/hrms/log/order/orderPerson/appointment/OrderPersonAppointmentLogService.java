package az.ingress.hrms.log.order.orderPerson.appointment;


import az.ingress.hrms.entity.order.orderPerson.OrderPersonAppointment;
import az.ingress.hrms.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderPersonAppointmentLogService {

    private final OrderPersonAppointmentLogRepository repository;

    @Transactional
    public void log(
            OrderPersonAppointment appointment,
            LogAction action,
            String performedBy
    ) {

        OrderPersonAppointmentLog log = OrderPersonAppointmentLog.builder()
                .mainId(appointment.getId())
                .orderId(
                        appointment.getOrder() != null
                                ? appointment.getOrder().getId()
                                : null
                )
                .personId(
                        appointment.getPerson() != null
                                ? appointment.getPerson().getId()
                                : null
                )
                .staffingPlanId(
                        appointment.getStaffingPlan() != null
                                ? appointment.getStaffingPlan().getId()
                                : null
                )
                .startDate(appointment.getStartDate())
                .endDate(appointment.getEndDate())
                .isClosed(appointment.getIsClosed())
                .dismissalOrderId(
                        appointment.getDismissalOrder() != null
                                ? appointment.getDismissalOrder().getId()
                                : null
                )
                .statusId(
                        appointment.getStatus() != null
                                ? appointment.getStatus().getId()
                                : null
                )
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .isDeleted(appointment.getIsDeleted())
                .deletedAt(appointment.getDeletedAt())
                .deletedBy(appointment.getDeletedBy())
                .actionType(action.name())
                .performedBy(performedBy)
                .loggedAt(LocalDateTime.now())
                .build();

        repository.save(log);
    }
}
