package az.ingress.hrms.log.person.personPhoto;

import az.ingress.hrms.entity.person.PersonPhoto;
import az.ingress.hrms.log.CurrentRequestProvider;
import az.ingress.hrms.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PersonPhotoLogService {

    private final PersonPhotoLogRepository repository;
    private final CurrentRequestProvider currentRequestProvider;

    @Transactional
    public void log(
            PersonPhoto personPhoto,
            LogAction action,
            String performedBy) {

        String ipAddress = currentRequestProvider.getIpAddress();

        PersonPhotoLog log = PersonPhotoLog.builder()
                .mainId(personPhoto.getId())
                .personId(personPhoto.getPerson() != null ? personPhoto.getPerson().getId() : null)
                .filePath(personPhoto.getFilePath())
                .isMain(Boolean.TRUE.equals(personPhoto.getIsMain()))
                .statusId(
                        personPhoto.getStatus() != null
                                ? personPhoto.getStatus().getId()
                                : null
                )
                .createdAt(personPhoto.getCreatedAt())
                .updatedAt(personPhoto.getUpdatedAt())
                .isDeleted(Boolean.TRUE.equals(personPhoto.getIsDeleted()))
                .deletedAt(personPhoto.getDeletedAt())
                .deletedBy(personPhoto.getDeletedBy())
                .actionType(action.name())
                .performedBy(performedBy)
                .ipAddress(ipAddress)
                .loggedAt(LocalDateTime.now())
                .build();

        repository.save(log);
    }
}