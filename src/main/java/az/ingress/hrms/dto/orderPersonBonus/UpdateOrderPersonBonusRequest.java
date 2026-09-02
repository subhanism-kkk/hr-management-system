package az.ingress.hrms.dto.orderPersonBonus;

import az.ingress.hrms.enums.BonusCalculationType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderPersonBonusRequest {

    @NotNull(message = "Person ID cannot be null.")
    @Positive(message = "Person ID must be positive.")
    private Integer personId;

    @NotNull(message = "Bonus ID cannot be null.")
    @Positive(message = "Bonus ID must be positive.")
    private Integer bonusTypeId;

    @NotNull
    private BonusCalculationType calculationType;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @Size(max = 500)
    private String reason;
}