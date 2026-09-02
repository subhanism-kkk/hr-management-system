package az.ingress.hrms.dto.criteria;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class OrderSearchCriteria {

    // Supports both 'search' and 'keyword'
    private String keyword;
    private String search;

    private Integer orderTypeId;

    private String orderTypeCode;
    private String type;

    private String orderNumber;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate orderDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate orderDateTo;

    private String statusCode;
    private String status;

    public String getEffectiveKeyword() {
        return search != null && !search.isBlank() ? search : keyword;
    }

    public String getEffectiveOrderTypeCode() {
        return type != null && !type.isBlank() ? type : orderTypeCode;
    }

    public String getEffectiveStatusCode() {
        return status != null && !status.isBlank() ? status : statusCode;
    }
}