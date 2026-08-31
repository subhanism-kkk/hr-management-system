package az.ingress.hrms.dto.orderPersonAppointment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPersonAppointmentUpdateRequest {

    @NotNull(message = "PersonId cannot be null.")
    @Positive(message = "PersonId must be positive.")
    private Integer personId;

    @NotNull(message = "StaffingPlanId cannot be null.")
    @Positive(message = "StaffingPlanId must be positive.")
    private Integer staffingPlanId;

    @NotNull(message = "Start date cannot be null.")
    private LocalDate startDate;

    private LocalDate endDate;
}
