package az.ingress.hrms.mapper;

import az.ingress.hrms.dto.orderType.OrderTypeRequest;
import az.ingress.hrms.dto.orderType.OrderTypeResponse;
import az.ingress.hrms.entity.order.OrderType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface OrderTypeMapper {

    OrderType toEntity(OrderTypeRequest request);

    OrderTypeResponse toResponse(OrderType orderType);

    void updateEntity(
            @MappingTarget OrderType orderType,
            OrderTypeRequest request
    );

}