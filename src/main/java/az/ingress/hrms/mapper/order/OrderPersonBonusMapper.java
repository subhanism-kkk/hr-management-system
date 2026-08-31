package az.ingress.hrms.mapper.order;

import az.ingress.hrms.dto.orderPersonBonus.CreateOrderPersonBonusRequest;
import az.ingress.hrms.dto.orderPersonBonus.OrderPersonBonusResponse;
import az.ingress.hrms.dto.orderPersonBonus.UpdateOrderPersonBonusRequest;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonBonus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface OrderPersonBonusMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "bonusType", ignore = true)
    @Mapping(target = "status", ignore = true)
    OrderPersonBonus toEntity(CreateOrderPersonBonusRequest request);

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "personId", source = "person.id")
    @Mapping(source = "person.fullName", target = "personName")
    @Mapping(target = "bonusTypeId", source = "bonusType.id")
    @Mapping(target = "bonusTypeName", source = "bonusType.name")
    @Mapping(target = "statusId", source = "status.id")
    @Mapping(target = "statusName", source = "status.name")
    OrderPersonBonusResponse toResponse(OrderPersonBonus entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "bonusType", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(@MappingTarget OrderPersonBonus entity, UpdateOrderPersonBonusRequest request);
}