package az.ingress.hrms.mapper;

import az.ingress.hrms.dto.orderType.OrderTypeRequest;
import az.ingress.hrms.dto.orderType.OrderTypeResponse;
import az.ingress.hrms.entity.lookup.OrderType;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface OrderTypeMapper {

    OrderTypeResponse toResponse(OrderType orderType);

    @Mapping(target = "id", ignore = true)
    OrderType toEntity(OrderTypeRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget OrderType orderType, OrderTypeRequest request);
}