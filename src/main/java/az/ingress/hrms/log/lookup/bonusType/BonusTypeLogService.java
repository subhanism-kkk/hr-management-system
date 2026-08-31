package az.ingress.hrms.log.lookup.bonusType;

import az.ingress.hrms.entity.lookup.BonusType;
import az.ingress.hrms.log.CurrentRequestProvider;
import az.ingress.hrms.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BonusTypeLogService {

    private final BonusTypeLogRepository repository;
    private final CurrentRequestProvider currentRequestProvider;

    @Transactional
    public void log(
            BonusType bonusType,
            LogAction action,
            String performedBy) {

        String ipAddress = currentRequestProvider.getIpAddress();

        BonusTypeLog log = BonusTypeLog.builder()
                .mainId(bonusType.getId())
                .name(bonusType.getName())
                .description(bonusType.getDescription())
                .createdAt(bonusType.getCreatedAt())
                .updatedAt(bonusType.getUpdatedAt())
                .isDeleted(bonusType.getIsDeleted())
                .deletedAt(bonusType.getDeletedAt())
                .deletedBy(bonusType.getDeletedBy())
                .actionType(action.name())
                .performedBy(performedBy)
                .loggedAt(LocalDateTime.now())
                .ipAddress(ipAddress)
                .build();

        repository.save(log);
    }
}