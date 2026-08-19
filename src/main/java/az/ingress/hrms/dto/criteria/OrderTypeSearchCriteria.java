package az.ingress.hrms.dto.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderTypeSearchCriteria {

    private String search;
    private String name;
    private String code;
}