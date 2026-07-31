package az.ingress.hrms.dto.orderPersonDismissal;

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
public class OrderPersonDismissalCreateRequest {

    @NotNull(message = "Order ID cannot be null.")
    @Positive(message = "Order ID must be positive.")
    private Integer orderId;

    @NotNull(message = "Person ID cannot be null.")
    @Positive(message = "Person ID must be positive.")
    private Integer personId;

    @NotNull(message = "Dismissal date cannot be null.")
    private LocalDate dismissalDate;

    @Size(max = 500, message = "Description cannot exceed 500 characters.")
    private String description;
}