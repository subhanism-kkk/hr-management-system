package az.ingress.hrms.mapper;

import az.ingress.hrms.dto.personContactInfo.PersonContactInfoUpdateRequest;
import az.ingress.hrms.dto.personContactInfo.PersonContactInfoCreateRequest;
import az.ingress.hrms.dto.personContactInfo.PersonContactInfoResponse;
import az.ingress.hrms.entity.person.PersonContactInfo;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PersonContactInfoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "contactType", ignore = true)
    PersonContactInfo toEntity(PersonContactInfoCreateRequest request);

    @Mapping(source = "person.id", target = "personId")
    @Mapping(source = "contactType.id", target = "contactTypeId")
    PersonContactInfoResponse toResponse(PersonContactInfo entity);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "contactType", ignore = true)
    void updateEntity(
            @MappingTarget PersonContactInfo entity,
            PersonContactInfoUpdateRequest request
    );

}