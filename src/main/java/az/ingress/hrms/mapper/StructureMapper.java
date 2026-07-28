package az.ingress.hrms.mapper;

import az.ingress.hrms.dto.StructureRequest;
import az.ingress.hrms.dto.StructureResponse;
import az.ingress.hrms.entity.organization.Structure;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface StructureMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentStructure", ignore = true)
    @Mapping(target = "childStructures", ignore = true)
    Structure toEntity(StructureRequest request);

    @Mapping(source = "parentStructure.id", target = "parentStructureId")
    @Mapping(source = "parentStructure.name", target = "parentStructureName")
    StructureResponse toResponse(Structure entity);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentStructure", ignore = true)
    @Mapping(target = "childStructures", ignore = true)
    void updateEntity(
            @MappingTarget Structure entity,
            StructureRequest request
    );
}
