package az.ingress.hrms.entity.order;

import az.ingress.hrms.entity.base.WorkflowEntity;
import az.ingress.hrms.entity.lookup.OrderType;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonAppointment;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonPromotion;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("is_deleted = false")
public class Order extends WorkflowEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "order_type_id",
            nullable = false
    )
    private OrderType orderType;

    @Column(
            name = "order_number",
            nullable = false,
            unique = true,
            length = 50)
    private String orderNumber;

    @OneToMany(
            mappedBy = "order",
            fetch = FetchType.LAZY
    )
    private List<OrderPersonAppointment> appointments =
            new ArrayList<>();

    @OneToMany(
            mappedBy = "dismissalOrder",
            fetch = FetchType.LAZY
    )
    private List<OrderPersonAppointment> dismissedAppointments =
            new ArrayList<>();

    @OneToMany(
            mappedBy = "order",
            fetch = FetchType.LAZY
    )
    private List<OrderPersonPromotion> promotions = new ArrayList<>();

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;
}