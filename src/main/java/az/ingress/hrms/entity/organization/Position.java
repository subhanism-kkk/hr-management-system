package az.ingress.hrms.entity.organization;

import az.ingress.hrms.entity.base.WorkflowEntity;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonPromotion;
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
@Table(name = "position")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@SQLRestriction("is_deleted = false")
public class Position extends WorkflowEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @OneToMany(
            mappedBy = "position",
            fetch = FetchType.LAZY
    )
    private List<StaffingPlan> staffingPlans = new ArrayList<>();

    @OneToMany(
            mappedBy = "oldPosition",
            fetch = FetchType.LAZY
    )
    private List<OrderPersonPromotion> oldPromotions = new ArrayList<>();

    @OneToMany(
            mappedBy = "newPosition",
            fetch = FetchType.LAZY
    )
    private List<OrderPersonPromotion> newPromotions = new ArrayList<>();
}
