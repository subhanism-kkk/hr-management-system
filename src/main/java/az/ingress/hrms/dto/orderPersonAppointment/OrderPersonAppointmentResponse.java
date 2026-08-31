package az.ingress.hrms.dto.orderPersonAppointment;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class OrderPersonAppointmentResponse {
    private Integer id;

    private Integer orderId;

    private String orderNumber;

    private Integer personId;

    private String personName;

    private Integer staffingPlanId;

    private String structureName;

    private String positionName;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean isClosed;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer dismissalOrderId;

    private Integer statusId;

    private String statusName;
}