package az.ingress.hrms.mapper;

import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.dto.person.PersonRequest;
import az.ingress.hrms.dto.person.PersonResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PersonMapper {

    Person toEntity(PersonRequest request);

    PersonResponse toResponse(Person person);

    void updateEntity(@MappingTarget Person person, PersonRequest request);
}
