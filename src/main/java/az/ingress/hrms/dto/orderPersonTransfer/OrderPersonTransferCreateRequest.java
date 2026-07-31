package az.ingress.hrms.dto.orderPersonTransfer;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPersonTransferCreateRequest {

    @NotNull(message = "Order ID cannot be null.")
    @Positive(message = "Order ID must be positive.")
    private Integer orderId;

    @NotNull(message = "Person ID cannot be null.")
    @Positive(message = "Person ID must be positive.")
    private Integer personId;

    @NotNull(message = "Old structure ID cannot be null.")
    @Positive(message = "Old structure ID must be positive.")
    private Integer oldStructureId;

    @NotNull(message = "New structure ID cannot be null.")
    @Positive(message = "New structure ID must be positive.")
    private Integer newStructureId;

    @NotNull(message = "Old position ID cannot be null.")
    @Positive(message = "Old position ID must be positive.")
    private Integer oldPositionId;

    @NotNull(message = "New position ID cannot be null.")
    @Positive(message = "New position ID must be positive.")
    private Integer newPositionId;

    @NotNull(message = "Effective date cannot be null.")
    @FutureOrPresent(message = "Effective date must be today or in the future.")
    private LocalDate effectiveDate;
}
