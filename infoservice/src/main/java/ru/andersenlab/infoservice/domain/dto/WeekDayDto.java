package ru.andersenlab.infoservice.domain.dto;

import ru.andersenlab.infoservice.domain.model.WeekDayName;

import java.time.LocalTime;

public record WeekDayDto(
        WeekDayName weekDayName,
        LocalTime openTime,
        LocalTime closeTime,
        LocalTime breakCloseTime,
        LocalTime breakOpenTime,
        boolean dayOff
) {}
