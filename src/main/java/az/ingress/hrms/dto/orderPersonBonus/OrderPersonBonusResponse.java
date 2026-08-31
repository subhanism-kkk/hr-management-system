package az.ingress.hrms.dto.orderPersonBonus;

import az.ingress.hrms.enums.BonusCalculationType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class OrderPersonBonusResponse {

    private Long id;

    private Integer orderId;

    private Long personId;

    private String personName;

    private Long bonusTypeId;

    private String bonusTypeName;

    private BonusCalculationType calculationType;

    private BigDecimal amount;

    private LocalDate startDate;

    private LocalDate endDate;

    private String reason;

    private Integer statusId;

    private String statusName;
}