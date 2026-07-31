package az.ingress.hrms.dto.orderPersonTransfer;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPersonTransferResponse {

    private Integer id;

    private Integer orderId;
    private String orderNumber;

    private Integer personId;
    private String personFullName;

    private Integer oldStructureId;
    private String oldStructureName;

    private Integer newStructureId;
    private String newStructureName;

    private Integer oldPositionId;
    private String oldPositionName;

    private Integer newPositionId;
    private String newPositionName;

    private LocalDate effectiveDate;
    private String statusCode;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}