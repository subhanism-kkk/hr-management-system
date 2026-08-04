package az.ingress.hrms.log.person.person;


import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.log.CurrentRequestProvider;
import az.ingress.hrms.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PersonLogService {

    private final PersonLogRepository repository;
    private final CurrentRequestProvider currentRequestProvider;

    @Transactional
    public void log(
            Person person,
            LogAction action,
            String performedBy) {

        String ipAddress = currentRequestProvider.getIpAddress();


        PersonLog log = PersonLog.builder()
                .mainId(person.getId())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .statusId(
                        person.getStatus() != null
                                ? person.getStatus().getId()
                                : null
                )
                .createdAt(person.getCreatedAt())
                .updatedAt(person.getUpdatedAt())
                .isDeleted(person.getIsDeleted())
                .deletedAt(person.getDeletedAt())
                .deletedBy(person.getDeletedBy())
                .actionType(action.name())
                .performedBy(performedBy)
                .loggedAt(LocalDateTime.now())
                .ipAddress(ipAddress)
                .build();

        repository.save(log);
    }
}
