package az.ingress.hrms.log.lookup.leaveType;

import az.ingress.hrms.entity.lookup.LeaveType;
import az.ingress.hrms.log.CurrentRequestProvider;
import az.ingress.hrms.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LeaveTypeLogService {

    private final LeaveTypeLogRepository repository;
    private final CurrentRequestProvider currentRequestProvider;


    @Transactional
    public void log(
            LeaveType leaveType,
            LogAction action,
            String performedBy) {


        String ipAddress = currentRequestProvider.getIpAddress();

        LeaveTypeLog log = LeaveTypeLog.builder()
                .mainId(leaveType.getId())
                .code(leaveType.getCode())
                .name(leaveType.getName())
                .description(leaveType.getDescription())
                .statusId(
                        leaveType.getStatus() != null
                                ? leaveType.getStatus().getId()
                                : null
                )
                .createdAt(leaveType.getCreatedAt())
                .updatedAt(leaveType.getUpdatedAt())
                .isDeleted(leaveType.getIsDeleted())
                .deletedAt(leaveType.getDeletedAt())
                .deletedBy(leaveType.getDeletedBy())
                .actionType(action.name())
                .performedBy(performedBy)
                .ipAddress(ipAddress)
                .loggedAt(LocalDateTime.now())
                .build();

        repository.save(log);
    }
}