package az.ingress.hrms.dto.order;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailResponse {

    private Integer id;
    private Integer orderTypeId;
    private String orderTypeName;
    private String orderTypeCode;
    private String orderNumber;
    private LocalDate orderDate;
    private Integer statusId;
    private String statusName;
    private List<JsonNode> details;
}
