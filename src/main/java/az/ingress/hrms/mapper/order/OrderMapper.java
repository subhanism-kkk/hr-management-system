package az.ingress.hrms.mapper.order;

import az.ingress.hrms.dto.order.OrderRequest;
import az.ingress.hrms.dto.order.OrderResponse;
import az.ingress.hrms.entity.order.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderType", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    Order toEntity(OrderRequest request);

    @Mapping(source = "orderType.id", target = "orderTypeId")
    @Mapping(source = "orderType.name", target = "orderTypeName")
    @Mapping(source = "orderType.code", target = "orderTypeCode")
    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "status.name", target = "statusName")
    OrderResponse toResponse(Order order);
}