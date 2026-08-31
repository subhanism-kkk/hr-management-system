package az.ingress.hrms.dto.criteria;

import az.ingress.hrms.enums.BonusCalculationType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class OrderPersonBonusSearchCriteria {

    private String search;
    private Integer personId;
    private Integer orderId;
    private Long bonusTypeId;
    private BonusCalculationType calculationType;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDateTo;
}