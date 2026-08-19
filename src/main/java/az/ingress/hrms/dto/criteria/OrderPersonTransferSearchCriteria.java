package az.ingress.hrms.dto.criteria;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderPersonTransferSearchCriteria {

    private String search;
    private Long orderId;
    private Integer personId;
    private Long oldStructureId;
    private String oldStructureName;
    private Long newStructureId;
    private String newStructureName;
    private Long oldPositionId;
    private String oldPositionName;
    private Long newPositionId;
    private String newPositionName;
    private String status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate effectiveDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate effectiveDateTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdTo;
}