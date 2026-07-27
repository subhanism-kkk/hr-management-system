package az.ingress.hrms.dto.position;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PositionRequest {

    @NotBlank(message = "name cannot be blank.")
    @Size(max = 100, message = "name cannot exceed 100 characters.")
    private String name;

    @NotBlank(message = "description cannot be blank.")
    @Size(max = 255, message = "description cannot exceed 255 characters.")
    private String description;
}
