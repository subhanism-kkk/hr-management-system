package az.ingress.hrms.entity.order.orderPerson;

import az.ingress.hrms.entity.base.WorkflowEntity;
import az.ingress.hrms.entity.order.Order;
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
@Table(name = "Order_Person_Dismissal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("is_deleted = false")
public class OrderPersonDismissal extends WorkflowEntity {

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

    @Column(
            name = "dismissal_date",
            nullable = false
    )
    private LocalDate dismissalDate;

    @Column(name = "description", length = 500)
    private String description;
}