package az.ingress.hrms.dto.orderPersonLeave;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPersonLeaveUpdateRequest {

    @NotNull(message = "Person ID cannot be null.")
    private Integer personId;

    @NotNull(message = "Leave type ID cannot be null.")
    @Positive(message = "Leave type ID must be positive.")
    private Integer leaveTypeId;

    @NotNull(message = "Start date cannot be null.")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null.")
    private LocalDate endDate;

    @Size(max = 500, message = "Reason cannot exceed 500 characters.")
    private String reason;
}