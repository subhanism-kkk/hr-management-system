package az.ingress.hrms.mapper.order;

import az.ingress.hrms.dto.orderPersonDismissal.OrderPersonDismissalCreateRequest;
import az.ingress.hrms.dto.orderPersonDismissal.OrderPersonDismissalResponse;
import az.ingress.hrms.dto.orderPersonDismissal.OrderPersonDismissalUpdateRequest;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonDismissal;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderPersonDismissalMapper {

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "orderNumber", source = "order.orderNumber")
    @Mapping(target = "personId", source = "person.id")
    @Mapping(target = "personFullName", source = "person.fullName")
    @Mapping(target = "statusName", source = "status.name")
    @Mapping(target = "statusId", source = "status.id")
    OrderPersonDismissalResponse toResponse(OrderPersonDismissal entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "status", ignore = true)
    OrderPersonDismissal toEntity(OrderPersonDismissalCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(@MappingTarget OrderPersonDismissal entity, OrderPersonDismissalUpdateRequest request);
}