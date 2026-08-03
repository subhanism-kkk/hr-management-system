package az.ingress.hrms.log.person.personContactInfo;

import az.ingress.hrms.entity.person.PersonContactInfo;
import az.ingress.hrms.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PersonContactInfoLogService {

    private final PersonContactInfoLogRepository repository;

    @Transactional
    public void log(
            PersonContactInfo contactInfo,
            LogAction action,
            String performedBy
    ) {

        PersonContactInfoLog log = PersonContactInfoLog.builder()
                .mainId(contactInfo.getId())
                .personId(
                        contactInfo.getPerson() != null
                                ? contactInfo.getPerson().getId()
                                : null
                )
                .contactTypeId(
                        contactInfo.getContactType() != null
                                ? contactInfo.getContactType().getId()
                                : null
                )
                .contactValue(contactInfo.getContactValue())
                .isPrimary(contactInfo.getIsPrimary())
                .createdAt(contactInfo.getCreatedAt())
                .updatedAt(contactInfo.getUpdatedAt())
                .isDeleted(contactInfo.getIsDeleted())
                .deletedAt(contactInfo.getDeletedAt())
                .deletedBy(contactInfo.getDeletedBy())
                .actionType(action.name())
                .performedBy(performedBy)
                .loggedAt(LocalDateTime.now())
                .build();

        repository.save(log);
    }
}