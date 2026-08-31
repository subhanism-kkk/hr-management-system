package az.ingress.hrms.dto.orderPersonSalary;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPersonSalaryResponse {

    private Integer id;

    private Integer orderId;
    private String orderNumber;

    private Integer staffingPlanId;

    private BigDecimal oldSalary;
    private BigDecimal newSalary;

    private LocalDate effectiveDate;

    private Integer statusId;
    private String statusName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}