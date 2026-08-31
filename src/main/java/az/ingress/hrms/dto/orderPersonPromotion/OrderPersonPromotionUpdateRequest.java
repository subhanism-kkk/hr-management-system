package az.ingress.hrms.dto.orderPersonPromotion;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class OrderPersonPromotionUpdateRequest {

    @NotNull(message = "Person ID cannot be null.")
    @Positive(message = "Person ID must be positive.")
    private Integer personId;

    @NotNull(message = "New position ID cannot be null.")
    @Positive(message = "New position ID must be positive.")
    private Integer newPositionId;

    @NotNull(message = "Effective date cannot be null.")
    @FutureOrPresent(message = "Effective date must be today or in the future.")
    private LocalDate effectiveDate;
}
