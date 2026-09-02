package az.ingress.hrms.mapper;

import az.ingress.hrms.dto.bonusType.BonusTypeRequest;
import az.ingress.hrms.dto.bonusType.BonusTypeResponse;
import az.ingress.hrms.entity.lookup.BonusType;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BonusTypeMapper {

    BonusTypeResponse toResponse(BonusType bonusType);

    @Mapping(target = "id", ignore = true)
    BonusType toEntity(BonusTypeRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget BonusType bonusType, BonusTypeRequest request);
}