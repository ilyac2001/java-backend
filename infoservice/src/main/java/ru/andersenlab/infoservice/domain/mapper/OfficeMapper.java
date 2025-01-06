package ru.andersenlab.infoservice.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.andersenlab.infoservice.domain.dto.OfficeOutputDTO;
import ru.andersenlab.infoservice.domain.model.Office;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {AddressMapper.class, ScheduleWeekDayMapper.class})
public interface OfficeMapper {

    @Mapping(source = "schedule.scheduleWeekDays", target = "workTime")
    OfficeOutputDTO toDto(Office office);
}
