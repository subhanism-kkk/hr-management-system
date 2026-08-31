package az.ingress.hrms.mapper.person;

import az.ingress.hrms.dto.personAddressInfo.PersonAddressInfoCreateRequest;
import az.ingress.hrms.dto.personAddressInfo.PersonAddressInfoResponse;
import az.ingress.hrms.dto.personAddressInfo.PersonAddressInfoUpdateRequest;
import az.ingress.hrms.entity.person.PersonAddressInfo;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PersonAddressInfoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "status", ignore = true)
    PersonAddressInfo toEntity(PersonAddressInfoCreateRequest request);

    @Mapping(source = "person.id", target = "personId")
    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "status.name", target = "statusName")
    PersonAddressInfoResponse toResponse(PersonAddressInfo entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(
            @MappingTarget PersonAddressInfo entity,
            PersonAddressInfoUpdateRequest request
    );
}