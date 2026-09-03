package az.ingress.hrms.entity.lookup;


import az.ingress.hrms.entity.base.SoftDeleteEntity;
import az.ingress.hrms.entity.base.WorkflowEntity;
import az.ingress.hrms.entity.person.PersonContactInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Contact_Types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("is_deleted = false")
public class ContactType extends WorkflowEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 255)
    private String description;

    @OneToMany(
            mappedBy = "contactType",
            fetch = FetchType.LAZY
    )
    private List<PersonContactInfo> personContacts = new ArrayList<>();
}
