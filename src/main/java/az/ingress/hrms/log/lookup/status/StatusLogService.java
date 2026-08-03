package az.ingress.hrms.log.lookup.status;

import az.ingress.hrms.entity.lookup.Status;
import az.ingress.hrms.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StatusLogService {

    private final StatusLogRepository repository;

    @Transactional
    public void log(
            Status status,
            LogAction action,
            String performedBy
    ) {

        StatusLog log = StatusLog.builder()
                .mainId(status.getId())
                .name(status.getName())
                .code(status.getCode())
                .createdAt(status.getCreatedAt())
                .updatedAt(status.getUpdatedAt())
                .isDeleted(status.getIsDeleted())
                .deletedAt(status.getDeletedAt())
                .deletedBy(status.getDeletedBy())
                .actionType(action.name())
                .performedBy(performedBy)
                .loggedAt(LocalDateTime.now())
                .build();

        repository.save(log);
    }
}