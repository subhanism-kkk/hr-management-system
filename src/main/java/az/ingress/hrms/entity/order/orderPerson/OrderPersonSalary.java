package az.ingress.hrms.entity.order.orderPerson;

import az.ingress.hrms.entity.base.WorkflowEntity;
import az.ingress.hrms.entity.order.Order;
import az.ingress.hrms.entity.organization.StaffingPlan;
import az.ingress.hrms.entity.person.Person;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Order_Person_Salary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("is_deleted = false")
public class OrderPersonSalary extends WorkflowEntity {

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

    @Column(
            name = "old_salary",
            nullable = false
    )
    private BigDecimal oldSalary;

    @Column(
            name = "new_salary",
            nullable = false
    )
    private BigDecimal newSalary;

    @Column(
            name = "effective_date",
            nullable = false
    )
    private LocalDate effectiveDate;
}