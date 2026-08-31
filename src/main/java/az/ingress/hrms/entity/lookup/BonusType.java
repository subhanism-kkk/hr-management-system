package az.ingress.hrms.entity.lookup;

import az.ingress.hrms.entity.base.WorkflowEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "Bonus_Type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("is_deleted = false")
public class BonusType extends WorkflowEntity {

    @Column(
            name = "name",
            nullable = false,
            length = 150
    )
    private String name;

    @Column(name = "description")
    private String description;
}