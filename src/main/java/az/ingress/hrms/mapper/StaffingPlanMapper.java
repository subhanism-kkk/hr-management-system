package az.ingress.hrms.mapper;

import az.ingress.hrms.dto.StaffingPlanCreateRequest;
import az.ingress.hrms.dto.StaffingPlanResponse;
import az.ingress.hrms.dto.StaffingPlanUpdateRequest;
import az.ingress.hrms.entity.organization.StaffingPlan;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StaffingPlanMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "structure", ignore = true)
    @Mapping(target = "position", ignore = true)
    StaffingPlan toEntity(StaffingPlanCreateRequest request);

    @Mapping(source = "structure.id", target = "structureId")
    @Mapping(source = "position.id", target = "positionId")
    StaffingPlanResponse toResponse(StaffingPlan entity);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "structure", ignore = true)
    @Mapping(target = "position", ignore = true)
    void updateEntity(
            @MappingTarget StaffingPlan entity,
            StaffingPlanUpdateRequest request
    );

}
