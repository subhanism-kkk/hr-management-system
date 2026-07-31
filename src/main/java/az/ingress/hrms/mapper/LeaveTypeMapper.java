package az.ingress.hrms.mapper;

import az.ingress.hrms.dto.leaveType.LeaveTypeCreateRequest;
import az.ingress.hrms.dto.leaveType.LeaveTypeResponse;
import az.ingress.hrms.dto.leaveType.LeaveTypeUpdateRequest;
import az.ingress.hrms.entity.lookup.LeaveType;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LeaveTypeMapper {

    LeaveTypeResponse toResponse(LeaveType entity);

    @Mapping(target = "id", ignore = true)
    LeaveType toEntity(LeaveTypeCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    void updateEntity(@MappingTarget LeaveType entity, LeaveTypeUpdateRequest request);
}