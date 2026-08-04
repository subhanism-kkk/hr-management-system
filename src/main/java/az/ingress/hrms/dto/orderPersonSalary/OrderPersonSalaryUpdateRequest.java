package az.ingress.hrms.dto.orderPersonSalary;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPersonSalaryUpdateRequest {

    @NotNull(message = "New salary cannot be null.")
    @Positive(message = "New salary must be positive.")
    private BigDecimal newSalary;

    @NotNull(message = "Effective date cannot be null.")
    @FutureOrPresent(message = "Effective date must be today or in the future.")
    private LocalDate effectiveDate;
}