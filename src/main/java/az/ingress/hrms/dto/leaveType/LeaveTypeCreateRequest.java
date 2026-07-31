package az.ingress.hrms.dto.leaveType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveTypeCreateRequest {

    @NotBlank(message = "Leave type code cannot be blank.")
    @Size(max = 50, message = "Code must not exceed 50 characters.")
    private String code;

    @NotBlank(message = "Leave type name cannot be blank.")
    @Size(max = 100, message = "Name must not exceed 100 characters.")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters.")
    private String description;
}