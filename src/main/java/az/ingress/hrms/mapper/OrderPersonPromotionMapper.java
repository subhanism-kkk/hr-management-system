package az.ingress.hrms.mapper;

import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionCreateRequest;
import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionResponse;
import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionUpdateRequest;
import az.ingress.hrms.entity.order.OrderPersonPromotion;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderPersonPromotionMapper {

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "orderNumber", source = "order.orderNumber")
    @Mapping(target = "personId", source = "person.id")
    @Mapping(source = "person.fullName", target = "personFullName")
    @Mapping(target = "oldPositionId", source = "oldPosition.id")
    @Mapping(target = "oldPositionName", source = "oldPosition.name")
    @Mapping(target = "newPositionId", source = "newPosition.id")
    @Mapping(target = "newPositionName", source = "newPosition.name")
    @Mapping(target = "statusCode", source = "status.code")
    OrderPersonPromotionResponse toResponse(OrderPersonPromotion entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "oldPosition", ignore = true)
    @Mapping(target = "newPosition", ignore = true)
    @Mapping(target = "status", ignore = true)
    OrderPersonPromotion toEntity(OrderPersonPromotionCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "oldPosition", ignore = true)
    @Mapping(target = "newPosition", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(@MappingTarget OrderPersonPromotion entity, OrderPersonPromotionUpdateRequest request);
}