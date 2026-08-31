package az.ingress.hrms.mapper.order;

import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryCreateRequest;
import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryResponse;
import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryUpdateRequest;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonSalary;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderPersonSalaryMapper {

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "orderNumber", source = "order.orderNumber")
    @Mapping(target = "staffingPlanId", source = "staffingPlan.id")
    @Mapping(target = "statusId", source = "status.id")
    @Mapping(target = "statusName", source = "status.name")
    OrderPersonSalaryResponse toResponse(OrderPersonSalary entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "staffingPlan", ignore = true)
    @Mapping(target = "status", ignore = true)
    OrderPersonSalary toEntity(OrderPersonSalaryCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "staffingPlan", ignore = true)
    @Mapping(target = "oldSalary", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(@MappingTarget OrderPersonSalary entity, OrderPersonSalaryUpdateRequest request);
}