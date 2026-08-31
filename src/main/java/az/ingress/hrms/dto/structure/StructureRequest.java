package az.ingress.hrms.dto.structure;

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
public class StructureRequest {

    @NotBlank(message = "Name cannot be blank.")
    @Size(max = 150)
    private String name;

    @NotNull(message = "Order Id cannot be Null.")
    @Positive(message = "Order Id must be positive.")
    private Integer orderId;

    // could be null bc its optional.
    private Integer parentStructureId;

    private Boolean isClosed;
}
