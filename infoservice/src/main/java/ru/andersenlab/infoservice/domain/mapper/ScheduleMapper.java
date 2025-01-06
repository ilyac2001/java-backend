package ru.andersenlab.infoservice.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.andersenlab.infoservice.domain.dto.ScheduleDto;
import ru.andersenlab.infoservice.domain.dto.WeekDayDto;
import ru.andersenlab.infoservice.domain.model.Schedule;
import ru.andersenlab.infoservice.domain.model.ScheduleWeekDay;
import ru.andersenlab.infoservice.domain.model.WeekDay;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {ScheduleWeekDayMapper.class, WeekDayMapper.class})
public interface ScheduleMapper {
    @Mapping(target = "weekDaysDto", source = "scheduleWeekDays")
    ScheduleDto toDto(Schedule schedule);

    default List<WeekDayDto> mapScheduleWeekDaysToDto(List<ScheduleWeekDay> scheduleWeekDays) {
        return scheduleWeekDays.stream()
                .map(scheduleWeekDay -> mapWeekDayToDto(scheduleWeekDay.getWeekDay()))
                .collect(Collectors.toList());
    }

    @Mapping(target = "weekDayName", source = "weekDay.weekDayName")
    WeekDayDto mapWeekDayToDto(WeekDay weekDay);
}
