package az.ingress.hrms.mapper.person;

import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.dto.person.PersonRequest;
import az.ingress.hrms.dto.person.PersonResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PersonMapper {
    @Mapping(target = "status", ignore = true)
    Person toEntity(PersonRequest request);

    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "status.name", target = "statusName")
    PersonResponse toResponse(Person person);

    @Mapping(target = "status", ignore = true)
    void updateEntity(@MappingTarget Person person, PersonRequest request);
}
