package az.ingress.hrms.log.person.personAddressInfo;

import az.ingress.hrms.entity.person.PersonAddressInfo;
import az.ingress.hrms.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PersonAddressInfoLogService {

    private final PersonAddressInfoLogRepository repository;

    @Transactional
    public void log(
            PersonAddressInfo addressInfo,
            LogAction action,
            String performedBy
    ) {

        PersonAddressInfoLog log = PersonAddressInfoLog.builder()
                .mainId(addressInfo.getId())
                .personId(
                        addressInfo.getPerson() != null
                                ? addressInfo.getPerson().getId()
                                : null
                )
                .address(addressInfo.getAddress())
                .createdAt(addressInfo.getCreatedAt())
                .updatedAt(addressInfo.getUpdatedAt())
                .isDeleted(addressInfo.getIsDeleted())
                .deletedAt(addressInfo.getDeletedAt())
                .deletedBy(addressInfo.getDeletedBy())
                .actionType(action.name())
                .performedBy(performedBy)
                .loggedAt(LocalDateTime.now())
                .build();

        repository.save(log);
    }
}