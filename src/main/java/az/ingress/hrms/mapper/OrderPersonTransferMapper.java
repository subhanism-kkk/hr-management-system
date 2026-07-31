package az.ingress.hrms.mapper;

import az.ingress.hrms.dto.orderPersonTransfer.OrderPersonTransferCreateRequest;
import az.ingress.hrms.dto.orderPersonTransfer.OrderPersonTransferResponse;
import az.ingress.hrms.dto.orderPersonTransfer.OrderPersonTransferUpdateRequest;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonTransfer;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderPersonTransferMapper {

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "orderNumber", source = "order.orderNumber")
    @Mapping(target = "personId", source = "person.id")
    @Mapping(source = "person.fullName", target = "personFullName")
    @Mapping(target = "oldStructureId", source = "oldStructure.id")
    @Mapping(target = "oldStructureName", source = "oldStructure.name")
    @Mapping(target = "newStructureId", source = "newStructure.id")
    @Mapping(target = "newStructureName", source = "newStructure.name")
    @Mapping(target = "oldPositionId", source = "oldPosition.id")
    @Mapping(target = "oldPositionName", source = "oldPosition.name")
    @Mapping(target = "newPositionId", source = "newPosition.id")
    @Mapping(target = "newPositionName", source = "newPosition.name")
    @Mapping(target = "statusCode", source = "status.code")
    OrderPersonTransferResponse toResponse(OrderPersonTransfer entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "oldStructure", ignore = true)
    @Mapping(target = "newStructure", ignore = true)
    @Mapping(target = "oldPosition", ignore = true)
    @Mapping(target = "newPosition", ignore = true)
    @Mapping(target = "status", ignore = true)
    OrderPersonTransfer toEntity(OrderPersonTransferCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "oldStructure", ignore = true)
    @Mapping(target = "newStructure", ignore = true)
    @Mapping(target = "oldPosition", ignore = true)
    @Mapping(target = "newPosition", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(@MappingTarget OrderPersonTransfer entity, OrderPersonTransferUpdateRequest request);
}