package az.ingress.hrms.dto.contactType;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactTypeRequest {

    @NotBlank(message = "Contact Type name cannot be blank.")
    @Size(max = 50, message = "Contact Type name cannot exceed 50 characters.")
    private String name;

    @Size(max = 255)
    private String description;
}
