package az.ingress.hrms.dto.order;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Integer id;

    private Integer orderTypeId;

    private String orderTypeName;

    private String orderTypeCode;

    private String orderNumber;

    private LocalDate orderDate;

    private Integer statusId;

    private String statusName;
}