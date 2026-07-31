package az.ingress.hrms.entity.organization;

import az.ingress.hrms.entity.base.WorkflowEntity;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonAppointment;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Staffing_Plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("is_deleted = false")
public class StaffingPlan extends WorkflowEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "structure_id",
            nullable = false
    )
    private Structure structure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "position_id",
            nullable = false
    )
    private Position position;

    @Column(
            name = "salary",
            nullable = false,
            precision = 12,
            scale = 2
    )    private BigDecimal salary;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "is_closed", nullable = false)
    @Builder.Default
    private Boolean isClosed = false;


    @OneToMany(
            mappedBy = "staffingPlan",
            fetch = FetchType.LAZY
    )
    private List<OrderPersonAppointment> appointments =
            new ArrayList<>();

}
