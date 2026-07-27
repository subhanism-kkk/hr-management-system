package az.ingress.hrms.mapper;


import az.ingress.hrms.dto.personPersonalInfo.PersonPersonalInfoCreateRequest;
import az.ingress.hrms.dto.personPersonalInfo.PersonPersonalInfoResponse;
import az.ingress.hrms.dto.personPersonalInfo.PersonPersonalInfoUpdateRequest;
import az.ingress.hrms.entity.person.PersonPersonalInfo;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PersonPersonalInfoMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    PersonPersonalInfo toEntity(PersonPersonalInfoCreateRequest request);

    @Mapping(source = "person.id", target = "personId")
    PersonPersonalInfoResponse toResponse(PersonPersonalInfo entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    void updateEntity(
            @MappingTarget PersonPersonalInfo entity,
            PersonPersonalInfoUpdateRequest request
    );

    }
