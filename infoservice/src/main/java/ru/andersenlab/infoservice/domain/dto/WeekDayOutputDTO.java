package ru.andersenlab.infoservice.domain.dto;

public record WeekDayOutputDTO(
        String weekDayName,
        String openTime,
        String closeTime,
        String breakOpenTime,
        String breakCloseTime
) {
}
