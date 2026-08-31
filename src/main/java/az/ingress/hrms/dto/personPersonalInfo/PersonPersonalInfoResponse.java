package az.ingress.hrms.dto.personPersonalInfo;

import az.ingress.hrms.enums.Gender;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonPersonalInfoResponse {

    private Integer id;

    private Integer personId;

    private Gender gender;

    private LocalDate dateOfBirth;

    private String finCode;

    private String statusName;

    private Integer statusId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}