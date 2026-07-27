package az.ingress.hrms.entity.order;

import az.ingress.hrms.entity.base.SoftDeleteOnlyEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "Orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("is_deleted = false")
public class Order extends SoftDeleteOnlyEntity {

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

}