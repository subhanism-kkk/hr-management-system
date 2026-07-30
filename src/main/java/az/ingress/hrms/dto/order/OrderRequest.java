package az.ingress.hrms.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    @NotNull(message = "Order Type is required.")
    @Positive
    private Integer orderTypeId;

    @NotNull(message = "Order date cannot be null.")
    @PastOrPresent(message = "Order date cannot be in the future.")
    private LocalDate orderDate;
}