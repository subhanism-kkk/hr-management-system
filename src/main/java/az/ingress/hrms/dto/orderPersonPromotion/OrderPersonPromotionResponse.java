package az.ingress.hrms.dto.orderPersonPromotion;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPersonPromotionResponse {

    private Integer id;

    private Integer orderId;
    private String orderNumber;

    private Integer personId;
    private String personFullName;

    private Integer oldPositionId;
    private String oldPositionName;

    private Integer newPositionId;
    private String newPositionName;

    private LocalDate effectiveDate;
    private String statusCode;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}