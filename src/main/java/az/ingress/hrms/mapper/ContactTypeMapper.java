package az.ingress.hrms.mapper;

import az.ingress.hrms.entity.lookup.ContactType;
import az.ingress.hrms.dto.contactType.ContactTypeRequest;
import az.ingress.hrms.dto.contactType.ContactTypeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ContactTypeMapper {

    @Mapping(target = "status", ignore = true)
    ContactType toEntity(ContactTypeRequest request);

    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "status.name", target = "statusName")
    ContactTypeResponse toResponse(ContactType contactType);

    @Mapping(target = "status", ignore = true)
    void updateEntity(@MappingTarget ContactType contactType, ContactTypeRequest request);
}
