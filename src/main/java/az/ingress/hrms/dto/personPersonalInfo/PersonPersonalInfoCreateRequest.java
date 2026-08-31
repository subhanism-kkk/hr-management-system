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

    @NotNull(message = "Person ID is required.")
    private Integer personId;

    @NotNull(message = "Gender is required.")
    private Gender gender;

    @NotNull(message = "Date of birth is required.")
    @Past(message = "Date of birth must be in the past.")
    private LocalDate dateOfBirth;

    @NotBlank(message = "cannot be blank.")
    @Pattern(
            regexp = "^[A-Z0-9]{7}$",
            message = "FIN code must contain exactly 7 uppercase letters or digits."
    )
    private String finCode;
}
