package az.ingress.hrms.dto.orderType;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderTypeResponse {

    private Integer id;

    private String name;

    private String statusName;

    private Integer statusId;

    private String description;

    private String code;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
