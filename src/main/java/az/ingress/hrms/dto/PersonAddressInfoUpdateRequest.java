package az.ingress.hrms.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonAddressInfoUpdateRequest {

    @NotBlank(message = "Address cannot be blank.")
    @Size(max = 500)
    private String address;
}
