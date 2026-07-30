package az.ingress.hrms.dto.orderPersonPromotion;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPersonPromotionCreateRequest {
    @NotNull(message = "OrderId cannot be null.")
    @Positive
    private Integer orderId;

    @NotNull(message = "PersonId cannot be null.")
    @Positive
    private Integer personId;

    @NotNull(message = "oldPositionId cannot be null.")
    @Positive
    private Integer oldPositionId;

    @NotNull(message = "newPositionId cannot be null.")
    @Positive
    private Integer newPositionId;

    @NotNull(message = "Effective date cannot be null.")
    private LocalDate effectiveDate;
}
