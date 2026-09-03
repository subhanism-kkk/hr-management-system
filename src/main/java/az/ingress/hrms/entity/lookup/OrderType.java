package az.ingress.hrms.entity.lookup;

import az.ingress.hrms.entity.base.SoftDeleteEntity;
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

@Getter
@Setter
@Entity
@Table(name = "Order_Types")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("is_deleted = false")
public class OrderType extends WorkflowEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false, unique = true, length = 10)
    private String code;
}