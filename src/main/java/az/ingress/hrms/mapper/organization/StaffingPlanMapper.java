package az.ingress.hrms.mapper.organization;

import az.ingress.hrms.dto.staffingPlan.StaffingPlanCreateRequest;
import az.ingress.hrms.dto.staffingPlan.StaffingPlanResponse;
import az.ingress.hrms.dto.staffingPlan.StaffingPlanUpdateRequest;
import az.ingress.hrms.entity.organization.StaffingPlan;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StaffingPlanMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "structure", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "status", ignore = true)
    StaffingPlan toEntity(StaffingPlanCreateRequest request);


    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "structure.id", target = "structureId")
    @Mapping(source = "structure.name", target = "structureName")
    @Mapping(source = "position.id", target = "positionId")
    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "position.name", target = "positionName")
    @Mapping(source = "status.name", target = "statusName")
    StaffingPlanResponse toResponse(StaffingPlan entity);


    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "structure", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(
            @MappingTarget StaffingPlan entity,
            StaffingPlanUpdateRequest request
    );
}