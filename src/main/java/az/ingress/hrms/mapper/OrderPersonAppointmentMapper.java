package az.ingress.hrms.mapper;

import az.ingress.hrms.dto.orderPersonAppointment.OrderPersonAppointmentCreateRequest;
import az.ingress.hrms.dto.orderPersonAppointment.OrderPersonAppointmentResponse;
import az.ingress.hrms.dto.orderPersonAppointment.OrderPersonAppointmentUpdateRequest;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonAppointment;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderPersonAppointmentMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "staffingPlan", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "dismissalOrder", ignore = true)
    OrderPersonAppointment toEntity(OrderPersonAppointmentCreateRequest request);


    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "order.orderNumber", target = "orderNumber")

    @Mapping(source = "person.id", target = "personId")
    @Mapping(source = "person.fullName", target = "personName")

    @Mapping(source = "staffingPlan.id", target = "staffingPlanId")
    @Mapping(source = "staffingPlan.structure.name", target = "structureName")
    @Mapping(source = "staffingPlan.position.name", target = "positionName")

    @Mapping(source = "dismissalOrder.id", target = "dismissalOrderId")
    OrderPersonAppointmentResponse toResponse(OrderPersonAppointment entity);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "staffingPlan", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "dismissalOrder", ignore = true)
    void updateEntity(
            @MappingTarget OrderPersonAppointment entity,
            OrderPersonAppointmentUpdateRequest request
    );
}
