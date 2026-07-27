package az.ingress.hrms.dto.order;

import az.ingress.hrms.dto.orderType.OrderTypeResponse;
import lombok.*;

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

}