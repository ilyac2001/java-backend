package ru.andersenlab.infoservice.domain.mapper;

import org.mapstruct.*;
import ru.andersenlab.infoservice.domain.dto.AtmDto;
import ru.andersenlab.infoservice.domain.model.Atm;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {AddressMapper.class})
public interface AtmMapper {
    @Mapping(source = "scheduleId", target = "schedule.scheduleId")
    @Mapping(source = "addressDetails", target = "address")
    Atm toEntity(AtmDto atmDto);

    @Mapping(source = "schedule.scheduleId", target = "scheduleId")
    @Mapping(source = "address", target = "addressDetails")
    AtmDto toDto(Atm atm);

    @Mapping(source = "scheduleId", target = "schedule.scheduleId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(source = "addressDetails", target = "address")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAtmFromDto(AtmDto atmDto, @MappingTarget Atm atm);
}
