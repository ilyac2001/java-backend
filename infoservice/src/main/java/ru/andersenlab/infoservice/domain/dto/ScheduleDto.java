package ru.andersenlab.infoservice.domain.dto;

import java.util.List;

public record ScheduleDto(
        String scheduleId,
        String scheduleName,
        List<WeekDayDto> weekDaysDto
) {}
