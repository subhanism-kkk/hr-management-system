package az.ingress.hrms.log.organization.staffingPlan;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Staffing_Plan_Log", schema = "log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffingPlanLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "main_id", nullable = false)
    private Integer mainId;

    @Column(name = "structure_id")
    private Integer structureId;

    @Column(name = "position_id")
    private Integer positionId;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "salary")
    private BigDecimal salary;

    @Column(name = "is_closed")
    private Boolean isClosed;

    @Column(name = "status_id")
    private Integer statusId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Column(name = "action_type", nullable = false)
    private String actionType;

    @Column(name = "performed_by", nullable = false)
    private String performedBy;

    @Column(name = "logged_at", nullable = false)
    private LocalDateTime loggedAt;
}