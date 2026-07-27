package az.ingress.hrms.mapper;

import az.ingress.hrms.dto.PersonAddressInfoCreateRequest;
import az.ingress.hrms.dto.PersonAddressInfoResponse;
import az.ingress.hrms.dto.PersonAddressInfoUpdateRequest;
import az.ingress.hrms.entity.person.PersonAddressInfo;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PersonAddressInfoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    PersonAddressInfo toEntity(PersonAddressInfoCreateRequest request);

    @Mapping(source = "person.id", target = "personId")
    PersonAddressInfoResponse toResponse(PersonAddressInfo entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    void updateEntity(
            @MappingTarget PersonAddressInfo entity,
            PersonAddressInfoUpdateRequest request
    );
}