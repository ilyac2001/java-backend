package ru.andersenlab.infoservice.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.andersenlab.infoservice.domain.dto.WeekDayDto;
import ru.andersenlab.infoservice.domain.dto.WeekDayOutputDTO;
import ru.andersenlab.infoservice.domain.model.ScheduleWeekDay;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {WeekDayMapper.class})
public interface ScheduleWeekDayMapper {
    WeekDayDto toDto(ScheduleWeekDay scheduleWeekDay);

    @Mapping(target = ".", source = "weekDay")
    WeekDayOutputDTO toWeekDayOutputDTO(ScheduleWeekDay scheduleWeekDay);
}
