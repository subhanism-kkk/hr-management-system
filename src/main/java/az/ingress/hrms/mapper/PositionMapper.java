package az.ingress.hrms.mapper;

import az.ingress.hrms.entity.organization.Position;
import az.ingress.hrms.dto.position.PositionRequest;
import az.ingress.hrms.dto.position.PositionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PositionMapper {

    Position toEntity(PositionRequest request);

    PositionResponse toResponse(Position position);

    void updateEntity(@MappingTarget Position position, PositionRequest request);

}
