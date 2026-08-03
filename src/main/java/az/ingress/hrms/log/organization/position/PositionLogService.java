package az.ingress.hrms.log.organization.position;


import az.ingress.hrms.entity.organization.Position;
import az.ingress.hrms.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PositionLogService {

    private final PositionLogRepository repository;

    @Transactional
    public void log(
            Position position,
            LogAction action,
            String performedBy
    ) {

        PositionLog log = PositionLog.builder()
                .mainId(position.getId())
                .name(position.getName())
                .description(position.getDescription())
                .createdAt(position.getCreatedAt())
                .updatedAt(position.getUpdatedAt())
                .isDeleted(position.getIsDeleted())
                .deletedAt(position.getDeletedAt())
                .deletedBy(position.getDeletedBy())
                .actionType(action.name())
                .performedBy(performedBy)
                .loggedAt(LocalDateTime.now())
                .build();

        repository.save(log);
    }
}