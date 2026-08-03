package az.ingress.hrms.log.lookup.contactType;

import az.ingress.hrms.entity.lookup.ContactType;
import az.ingress.hrms.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContactTypeLogService {

    private final ContactTypeLogRepository repository;

    @Transactional
    public void log(
            ContactType contactType,
            LogAction action,
            String performedBy
    ) {

        ContactTypeLog log = ContactTypeLog.builder()
                .mainId(contactType.getId())
                .name(contactType.getName())
                .description(contactType.getDescription())
                .createdAt(contactType.getCreatedAt())
                .updatedAt(contactType.getUpdatedAt())
                .isDeleted(contactType.getIsDeleted())
                .deletedAt(contactType.getDeletedAt())
                .deletedBy(contactType.getDeletedBy())
                .actionType(action.name())
                .performedBy(performedBy)
                .loggedAt(LocalDateTime.now())
                .build();

        repository.save(log);
    }
}