package az.ingress.hrms.dto.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private String statusName;
    private Integer statusId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
