package az.ingress.hrms.mapper;

import az.ingress.hrms.dto.order.OrderResponse;
import az.ingress.hrms.entity.order.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {

    @Mapping(source = "orderType.id", target = "orderTypeId")
    @Mapping(source = "orderType.name", target = "orderTypeName")
    @Mapping(source = "orderType.code", target = "orderTypeCode")
    OrderResponse toResponse(Order order);

}