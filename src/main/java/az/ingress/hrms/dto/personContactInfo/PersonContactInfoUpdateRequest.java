package az.ingress.hrms.dto.personContactInfo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonContactInfoUpdateRequest {
    @NotNull(message = "Contact Type ID is required.")
    private Integer contactTypeId;

    @NotBlank(message = "Contact value cannot be blank.")
    private String contactValue;

    @NotNull(message = "isPrimary flag must be provided.")
    private Boolean isPrimary;
}
