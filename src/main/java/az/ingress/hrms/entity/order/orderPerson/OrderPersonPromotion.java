package az.ingress.hrms.entity.order.orderPerson;


import az.ingress.hrms.entity.base.WorkflowEntity;
import az.ingress.hrms.entity.order.Order;
import az.ingress.hrms.entity.organization.Position;
import az.ingress.hrms.entity.person.Person;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Table(name = "Order_Person_Promotion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("is_deleted = false")
public class OrderPersonPromotion extends WorkflowEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "order_id",
            nullable = false
    )
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "person_id",
            nullable = false
    )
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "old_position_id",
            nullable = false
    )
    private Position oldPosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "new_position_id",
            nullable = false
    )
    private Position newPosition;

    @Column(
            name = "effective_date",
            nullable = false
    )
    private LocalDate effectiveDate;


}
