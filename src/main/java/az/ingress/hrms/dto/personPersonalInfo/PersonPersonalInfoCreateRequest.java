package az.ingress.hrms.dto.personPersonalInfo;


import az.ingress.hrms.enums.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonPersonalInfoCreateRequest {

    @NotNull
    private Integer personId;

    @NotNull
    private Gender gender;

    @NotNull
    @Past
    private LocalDate dateOfBirth;

    @NotBlank(message = "cannot be blank.")
    @Pattern(
            regexp = "^[A-Z0-9]{7}$",
            message = "FIN code must contain exactly 7 uppercase letters or digits."
    )
    private String finCode;
}
