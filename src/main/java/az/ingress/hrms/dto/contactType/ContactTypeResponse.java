package az.ingress.hrms.dto.contactType;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactTypeResponse {

    private Integer id;

    private String name;

    private String statusName;

    private Integer statusId;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
