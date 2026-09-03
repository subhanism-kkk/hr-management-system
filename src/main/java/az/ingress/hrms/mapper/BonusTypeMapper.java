package az.ingress.hrms.mapper;

import az.ingress.hrms.dto.bonusType.BonusTypeRequest;
import az.ingress.hrms.dto.bonusType.BonusTypeResponse;
import az.ingress.hrms.entity.lookup.BonusType;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BonusTypeMapper {

    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "status.name", target = "statusName")
    BonusTypeResponse toResponse(BonusType bonusType);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    BonusType toEntity(BonusTypeRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(@MappingTarget BonusType bonusType, BonusTypeRequest request);
}