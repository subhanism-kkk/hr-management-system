package az.ingress.hrms.dto.staffingPlan;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffingPlanResponse {
    private Integer id;
    private Integer orderId;
    private Integer structureId;
    private String structureName;
    private Integer positionId;
    private String positionName;
    private BigDecimal salary;
    private Integer capacity;
    private Boolean isClosed;
    private Integer statusId;
    private String statusName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}