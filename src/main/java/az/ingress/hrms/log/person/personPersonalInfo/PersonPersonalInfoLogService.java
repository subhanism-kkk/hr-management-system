package az.ingress.hrms.log.person.personPersonalInfo;

import az.ingress.hrms.entity.person.PersonPersonalInfo;
import az.ingress.hrms.log.CurrentRequestProvider;
import az.ingress.hrms.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PersonPersonalInfoLogService {

    private final PersonPersonalInfoLogRepository repository;
    private final CurrentRequestProvider currentRequestProvider;

    @Transactional
    public void log(
            PersonPersonalInfo personalInfo,
            LogAction action,
            String performedBy) {

        String ipAddress = currentRequestProvider.getIpAddress();

        PersonPersonalInfoLog log = PersonPersonalInfoLog.builder()
                .mainId(personalInfo.getId())
                .personId(
                        personalInfo.getPerson() != null
                                ? personalInfo.getPerson().getId()
                                : null
                )
                .gender(personalInfo.getGender())
                .dateOfBirth(personalInfo.getDateOfBirth())
                .finCode(personalInfo.getFinCode())
                .statusId(
                        personalInfo.getStatus() != null
                                ? personalInfo.getStatus().getId()
                                : null
                )
                .createdAt(personalInfo.getCreatedAt())
                .updatedAt(personalInfo.getUpdatedAt())
                .isDeleted(personalInfo.getIsDeleted())
                .deletedAt(personalInfo.getDeletedAt())
                .deletedBy(personalInfo.getDeletedBy())
                .actionType(action.name())
                .performedBy(performedBy)
                .ipAddress(ipAddress)
                .loggedAt(LocalDateTime.now())
                .build();

        repository.save(log);
    }
}