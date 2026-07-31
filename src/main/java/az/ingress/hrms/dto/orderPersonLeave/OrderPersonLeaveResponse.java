package az.ingress.hrms.dto.orderPersonLeave;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPersonLeaveResponse {

    private Integer id;

    private Integer orderId;
    private String orderNumber;

    private Integer personId;
    private String personFullName;

    private Integer leaveTypeId;
    private String leaveTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;

    private String statusCode;
    private LocalDateTime createdAt;
}