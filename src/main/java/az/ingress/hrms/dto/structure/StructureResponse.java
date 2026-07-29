package az.ingress.hrms.dto.structure;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StructureResponse {

    private Integer id;
    private String name;
    private Integer parentStructureId;
    private String parentStructureName;
    private Boolean isClosed;
    private LocalDateTime createdAt;
}
