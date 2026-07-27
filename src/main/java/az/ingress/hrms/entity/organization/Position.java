package az.ingress.hrms.entity.organization;

import az.ingress.hrms.entity.base.SoftDeleteEntity;
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
@Table(name = "Positions")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@SQLRestriction("is_deleted = false")
public class Position extends SoftDeleteEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;
}
