package az.ingress.hrms.entity.organization;

import az.ingress.hrms.entity.base.SoftDeleteEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Structure")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("is_deleted = false")
public class Structure extends SoftDeleteEntity {

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_structure_id")
    private Structure parentStructure;

    @OneToMany(mappedBy = "parentStructure", fetch = FetchType.LAZY)
    private List<Structure> childStructures;

    @Column(name = "is_closed", nullable = false)
    @Builder.Default
    private Boolean isClosed = false;

    @OneToMany(
            mappedBy = "structure",
            fetch = FetchType.LAZY
    )
    private List<StaffingPlan> staffingPlans = new ArrayList<>();
}
