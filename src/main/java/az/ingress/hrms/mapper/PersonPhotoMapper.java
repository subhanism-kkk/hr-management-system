package az.ingress.hrms.mapper;

import az.ingress.hrms.dto.personPhoto.PersonPhotoCreateRequest;
import az.ingress.hrms.dto.personPhoto.PersonPhotoResponse;
import az.ingress.hrms.dto.personPhoto.PersonPhotoUpdateRequest;
import az.ingress.hrms.entity.person.PersonPhoto;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PersonPhotoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    PersonPhoto toEntity(PersonPhotoCreateRequest request);

    @Mapping(source = "person.id", target = "personId")
    PersonPhotoResponse toResponse(PersonPhoto personPhoto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "person",ignore = true)
    void updateEntity(
            @MappingTarget PersonPhoto personPhoto,
            PersonPhotoUpdateRequest personPhotoUpdateRequest
    );
}
