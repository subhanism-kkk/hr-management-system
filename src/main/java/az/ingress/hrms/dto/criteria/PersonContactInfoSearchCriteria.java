package az.ingress.hrms.dto.criteria;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class PersonContactInfoSearchCriteria {

    private String search;
    private Integer personId;
    private Integer contactTypeId;
    private Boolean isPrimary;
    private String status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdTo;
}