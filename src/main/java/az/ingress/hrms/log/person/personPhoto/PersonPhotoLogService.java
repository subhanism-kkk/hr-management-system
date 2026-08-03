package az.ingress.hrms.log.person.personPhoto;

import az.ingress.hrms.entity.person.PersonPhoto;
import az.ingress.hrms.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PersonPhotoLogService {

    private final PersonPhotoLogRepository repository;

    @Transactional
    public void log(
            PersonPhoto personPhoto,
            LogAction action,
            String performedBy
    ) {

        PersonPhotoLog log = PersonPhotoLog.builder()
                .mainId(personPhoto.getId())
                .personId(personPhoto.getPerson().getId())
                .filePath(personPhoto.getFilePath())
                .statusId(
                        personPhoto.getStatus() != null
                                ? personPhoto.getStatus().getId()
                                : null
                )
                .createdAt(personPhoto.getCreatedAt())
                .updatedAt(personPhoto.getUpdatedAt())
                .isDeleted(personPhoto.getIsDeleted())
                .deletedAt(personPhoto.getDeletedAt())
                .deletedBy(personPhoto.getDeletedBy())
                .actionType(action.name())
                .performedBy(performedBy)
                .loggedAt(LocalDateTime.now())
                .build();

        repository.save(log);
    }
}
