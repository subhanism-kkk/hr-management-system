package az.ingress.hrms.mapper.organization;

import az.ingress.hrms.dto.structure.StructureRequest;
import az.ingress.hrms.dto.structure.StructureResponse;
import az.ingress.hrms.entity.organization.Structure;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface StructureMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "parentStructure", ignore = true)
    @Mapping(target = "childStructures", ignore = true)
    @Mapping(target = "status", ignore = true)
    Structure toEntity(StructureRequest request);

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "parentStructure.id", target = "parentStructureId")
    @Mapping(source = "parentStructure.name", target = "parentStructureName")
    @Mapping(source = "status.name", target = "statusName")
    @Mapping(source = "status.id", target = "statusId")
    StructureResponse toResponse(Structure entity);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "parentStructure", ignore = true)
    @Mapping(target = "childStructures", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(
            @MappingTarget Structure entity,
            StructureRequest request
    );
}
