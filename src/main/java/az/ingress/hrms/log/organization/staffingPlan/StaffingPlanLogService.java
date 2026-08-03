package az.ingress.hrms.log.organization.staffingPlan;

import az.ingress.hrms.entity.organization.StaffingPlan;
import az.ingress.hrms.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StaffingPlanLogService {

    private final StaffingPlanLogRepository repository;

    @Transactional
    public void log(
            StaffingPlan staffingPlan,
            LogAction action,
            String performedBy
    ) {

        StaffingPlanLog log = StaffingPlanLog.builder()
                .mainId(staffingPlan.getId())
                .structureId(
                        staffingPlan.getStructure() != null
                                ? staffingPlan.getStructure().getId()
                                : null
                )
                .positionId(
                        staffingPlan.getPosition() != null
                                ? staffingPlan.getPosition().getId()
                                : null
                )
                .capacity(staffingPlan.getCapacity())
                .salary(staffingPlan.getSalary())
                .isClosed(staffingPlan.getIsClosed())
                .statusId(
                        staffingPlan.getStatus() != null
                                ? staffingPlan.getStatus().getId()
                                : null
                )
                .createdAt(staffingPlan.getCreatedAt())
                .updatedAt(staffingPlan.getUpdatedAt())
                .isDeleted(staffingPlan.getIsDeleted())
                .deletedAt(staffingPlan.getDeletedAt())
                .deletedBy(staffingPlan.getDeletedBy())
                .actionType(action.name())
                .performedBy(performedBy)
                .loggedAt(LocalDateTime.now())
                .build();

        repository.save(log);
    }
}