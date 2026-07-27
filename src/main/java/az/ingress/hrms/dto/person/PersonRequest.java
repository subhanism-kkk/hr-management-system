package az.ingress.hrms.dto.person;

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
public class PersonRequest {

    @NotBlank(message = "First name cannot be blank.")
    @Size(max = 100, message = "First name cannot exceed 100 characters.")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank.")
    @Size(max = 100, message = "Last name cannot exceed 100 characters.")
    private String lastName;
}