package az.ingress.hrms.dto.bonusType;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BonusTypeResponse {

    private Integer id;

    private String name;

    private String statusName;

    private Integer statusId;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}