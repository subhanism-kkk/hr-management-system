package az.ingress.hrms.mapper.order;

import az.ingress.hrms.dto.orderPersonLeave.OrderPersonLeaveCreateRequest;
import az.ingress.hrms.dto.orderPersonLeave.OrderPersonLeaveResponse;
import az.ingress.hrms.dto.orderPersonLeave.OrderPersonLeaveUpdateRequest;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonLeave;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderPersonLeaveMapper {

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "orderNumber", source = "order.orderNumber")
    @Mapping(target = "personId", source = "person.id")
    @Mapping(target = "personFullName", source = "person.fullName")
    @Mapping(target = "leaveTypeId", source = "leaveType.id")
    @Mapping(target = "leaveTypeName", source = "leaveType.name")
    @Mapping(target = "statusName", source = "status.name")
    @Mapping(target = "statusId", source = "status.id")
    OrderPersonLeaveResponse toResponse(OrderPersonLeave entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "leaveType", ignore = true)
    @Mapping(target = "status", ignore = true)
    OrderPersonLeave toEntity(OrderPersonLeaveCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "leaveType", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(@MappingTarget OrderPersonLeave entity, OrderPersonLeaveUpdateRequest request);
}