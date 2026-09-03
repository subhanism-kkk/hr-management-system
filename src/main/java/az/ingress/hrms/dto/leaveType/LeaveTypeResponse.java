package az.ingress.hrms.dto.leaveType;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveTypeResponse {

    private Integer id;
    private String code;
    private String name;
    private String statusName;
    private Integer statusId;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}