package az.ingress.hrms.mapper;

import az.ingress.hrms.entity.lookup.Status;
import az.ingress.hrms.dto.status.StatusRequest;
import az.ingress.hrms.dto.status.StatusResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface StatusMapper {
    Status toEntity(StatusRequest request);

    StatusResponse toResponse(Status status);

    void updateEntity(@MappingTarget Status status, StatusRequest request);
}
