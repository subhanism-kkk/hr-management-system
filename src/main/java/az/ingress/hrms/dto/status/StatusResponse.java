package az.ingress.hrms.dto.status;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusResponse {

    private Integer id;

    private String name;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}