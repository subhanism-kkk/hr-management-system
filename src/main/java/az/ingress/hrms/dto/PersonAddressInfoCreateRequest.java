package az.ingress.hrms.dto;


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
public class PersonAddressInfoCreateRequest {

    @NotNull(message = "Person ID cannot be null.")
    @Positive
    private Integer personId;

    @NotBlank(message = "Address cannot be blank.")
    @Size(max = 500)
    private String address;
}