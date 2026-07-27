package az.ingress.hrms.dto.orderType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTypeRequest {

    @NotBlank(message = "Order name cannot be blank." )
    @Size(max = 100, message = "Contact Type name cannot exceed 50 characters.")
    private String name;

    @Size(max = 255)
    private String description;

    @NotBlank(message = "Code is required.")
    @Size(max = 10)
    private String code;
}
