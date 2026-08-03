package az.ingress.hrms.log.organization.structure;

import az.ingress.hrms.entity.organization.Structure;
import az.ingress.hrms.log.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StructureLogService {

    private final StructureLogRepository repository;

    @Transactional
    public void log(
            Structure structure,
            LogAction action,
            String performedBy
    ) {

        StructureLog log = StructureLog.builder()
                .mainId(structure.getId())
                .name(structure.getName())
                .parentId(
                        structure.getParentStructure() != null
                                ? structure.getParentStructure().getId()
                                : null
                )
                .isClosed(structure.getIsClosed())
                .statusId(
                        structure.getStatus() != null
                                ? structure.getStatus().getId()
                                : null
                )
                .createdAt(structure.getCreatedAt())
                .updatedAt(structure.getUpdatedAt())
                .isDeleted(structure.getIsDeleted())
                .deletedAt(structure.getDeletedAt())
                .deletedBy(structure.getDeletedBy())
                .actionType(action.name())
                .performedBy(performedBy)
                .loggedAt(LocalDateTime.now())
                .build();

        repository.save(log);
    }
}