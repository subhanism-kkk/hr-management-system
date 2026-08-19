package az.ingress.hrms.entity.order.orderPerson;

import az.ingress.hrms.entity.base.WorkflowEntity;
import az.ingress.hrms.entity.order.Order;
import az.ingress.hrms.entity.organization.StaffingPlan;
import az.ingress.hrms.entity.person.Person;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Table(name = "Order_Person_Appointment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("is_deleted = false")
public class OrderPersonAppointment extends WorkflowEntity {

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
                name = "staffing_plan_id",
                nullable = false
        )
        private StaffingPlan staffingPlan;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_closed", nullable = false)
    @Builder.Default
    private Boolean isClosed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dismissal_order_id", nullable = true)
    private Order dismissalOrder;

}
