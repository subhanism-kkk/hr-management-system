package az.ingress.hrms.entity.person;

import az.ingress.hrms.entity.base.SoftDeleteEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "Person_Photos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("is_deleted = false")
public class PersonPhoto extends SoftDeleteEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id",
    nullable = false)
    private Person person;

    @Column(
            name = "file_path",
            nullable = false,
            length = 500
    )
    private String filePath;

    @Column(name = "is_main")
    private Boolean isMain = false;
}
