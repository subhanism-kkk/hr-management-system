package az.ingress.hrms.dto.bonusType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonusTypeRequest {

    @NotBlank(message = "Bonus type name cannot be blank.")
    @Size(max = 150, message = "Bonus type name cannot exceed 150 characters.")
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters.")
    private String description;
}