package az.ingress.hrms.entity.person;

import az.ingress.hrms.entity.base.SoftDeleteEntity;
import az.ingress.hrms.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Table(name = "Person_Personal_Info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("is_deleted = false")
public class PersonPersonalInfo extends SoftDeleteEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "person_id",
            nullable = false,
            unique = true
    )
    private Person person;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "fin_code", nullable = false, unique = true, length = 7)
    @Pattern(
            regexp = "^[A-Z0-9]{7}$",
            message = "FIN code must contain exactly 7 uppercase letters or digits."
    )
    private String finCode;

}