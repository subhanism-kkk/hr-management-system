package az.ingress.hrms.dto.position;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PositionResponse {
    private Integer id;
    private String name;
    private String description;
    private String statusName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}