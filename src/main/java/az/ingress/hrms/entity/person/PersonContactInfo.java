package az.ingress.hrms.entity.person;

import az.ingress.hrms.entity.base.SoftDeleteEntity;
import az.ingress.hrms.entity.base.WorkflowEntity;
import az.ingress.hrms.entity.lookup.ContactType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "Person_Contact_Info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("is_deleted = false")
public class PersonContactInfo extends WorkflowEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id",
    nullable = false)
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_type_id",
    nullable = false)
    private ContactType contactType;

    @Column(
            name = "contact_value",
            nullable = false,
            length = 255
    )
    private String contactValue;

    @Column(name = "is_primary")
    private Boolean isPrimary = false;
}
