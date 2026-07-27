package az.ingress.hrms.dto.personPersonalInfo;


import az.ingress.hrms.enums.Gender;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonPersonalInfoUpdateRequest {

    @NotNull
    private Gender gender;

    @NotNull
    @Past
    private LocalDate dateOfBirth;
}
