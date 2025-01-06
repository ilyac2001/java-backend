package ru.andersenlab.infoservice.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.andersenlab.infoservice.domain.dto.WeekDayOutputDTO;
import ru.andersenlab.infoservice.domain.dto.WeekDayDto;
import ru.andersenlab.infoservice.domain.model.WeekDay;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WeekDayMapper {

    @Mapping(target = "weekDayName", source = "weekDayName")
    @Mapping(target = "openTime", source = "openTime")
    @Mapping(target = "closeTime", source = "closeTime")
    @Mapping(target = "breakOpenTime", source = "breakOpenTime")
    @Mapping(target = "breakCloseTime", source = "breakCloseTime")
    WeekDayOutputDTO toWeekDayOutputDTO(WeekDay weekDay);

    WeekDayDto toDto(WeekDay weekDay);
}
