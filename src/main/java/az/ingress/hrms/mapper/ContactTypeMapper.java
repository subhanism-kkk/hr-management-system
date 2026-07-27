package az.ingress.hrms.mapper;

import az.ingress.hrms.entity.lookup.ContactType;
import az.ingress.hrms.dto.contactType.ContactTypeRequest;
import az.ingress.hrms.dto.contactType.ContactTypeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ContactTypeMapper {
    ContactType toEntity(ContactTypeRequest request);

    ContactTypeResponse toResponse(ContactType contactType);

    void updateEntity(@MappingTarget ContactType contactType, ContactTypeRequest request);
}
