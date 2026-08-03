package az.ingress.hrms.log.order.order;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Orders_Log", schema = "log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "main_id", nullable = false)
    private Integer mainId;

    @Column(name = "order_type_id")
    private Integer orderTypeId;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "order_date")
    private LocalDate orderDate;

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