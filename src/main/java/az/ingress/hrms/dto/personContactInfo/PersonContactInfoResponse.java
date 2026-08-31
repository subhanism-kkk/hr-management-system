package az.ingress.hrms.dto.personContactInfo;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonContactInfoResponse {
    private Integer id;
    private Integer personId;
    private Integer contactTypeId;
    private String contactValue;
    private Boolean isPrimary;
    private String statusName;
    private Integer statusId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
