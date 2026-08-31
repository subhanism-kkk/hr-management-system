package az.ingress.hrms.dto.orderPersonDismissal;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPersonDismissalResponse {

    private Integer id;

    private Integer orderId;
    private String orderNumber;

    private Integer personId;
    private String personFullName;

    private LocalDate dismissalDate;
    private String description;

    private Integer statusId;
    private String statusName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}