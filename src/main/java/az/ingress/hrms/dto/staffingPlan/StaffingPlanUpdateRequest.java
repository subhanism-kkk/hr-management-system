package az.ingress.hrms.dto.staffingPlan;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffingPlanUpdateRequest {

    @NotNull(message = "Structure Id cannot be Null.")
    @Positive
    private Integer structureId;

    @NotNull(message = "Position Id cannot be Null.")
    @Positive
    private Integer positionId;

    @NotNull(message = "Salary cannot be Null.")
    @Positive(message = "Salary must be greater than zero.")
    private BigDecimal salary;

    @NotNull(message = "Capacity cannot be Null.")
    @Min(value = 1, message = "Capacity must be at least 1.")
    private Integer capacity;

}