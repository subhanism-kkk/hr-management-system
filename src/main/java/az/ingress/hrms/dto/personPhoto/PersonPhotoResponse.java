package az.ingress.hrms.dto.personPhoto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonPhotoResponse {
    private Integer id;
    private Integer personId;
    private String filePath;
    private Boolean isMain;
    private String statusName;
    private Integer statusId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

