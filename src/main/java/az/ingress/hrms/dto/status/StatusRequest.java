package az.ingress.hrms.dto.status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusRequest {

    @NotBlank(message = "Status name cannot be blank.")
    @Size(max = 100, message = "Status name cannot exceed 100 characters.")
    private String name;

}