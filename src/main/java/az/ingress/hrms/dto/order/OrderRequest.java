package az.ingress.hrms.dto.order;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    @NotNull(message = "Order Type is required.")
    @Positive(message = "Order Type must be positive.")
    private Integer orderTypeId;

    @NotNull(message = "Order date cannot be null.")
    @PastOrPresent(message = "Order date cannot be in the future.")
    private LocalDate orderDate;

    @NotNull(message = "Order data cannot be null.")
    @NotEmpty(message = "Order data cannot be empty.")
    private List<JsonNode> data;
}