package az.ingress.hrms.dto.personPhoto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonPhotoCreateRequest {

    @NotNull(message = "PersonId cannot be null.")
    @Positive
    private Integer personId;

    @NotBlank(message = "File path cannot be blank.")
    @Size(max = 500)
    private String filePath;

    @NotNull(message = "isMain flag must be provided.")
    private Boolean isMain;

}
