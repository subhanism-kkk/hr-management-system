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
public class OrderPersonAppointmentCreateRequest {

    @NotNull(message = "PersonId cannot be null.")
    @Positive
    private Integer personId;

    @NotNull(message = "StaffingPlanId cannot be null.")
    @Positive
    private Integer staffingPlanId;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;
}
