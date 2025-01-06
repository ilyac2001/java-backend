package ru.andersenlab.infoservice.domain.dto;

import java.time.LocalDateTime;

public record OutputAtmDto(
        String atmName,
        String locationDescription,
        AddressDto addressDetails,
        ScheduleDto scheduleDetails,
        boolean isDeleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public OutputAtmDto(AtmDto atmDto, ScheduleDto scheduleDto) {
        this(
                atmDto.atmName(),
                atmDto.locationDescription(),
                atmDto.addressDetails(),
                scheduleDto,
                atmDto.isDeleted(),
                atmDto.createdAt(),
                atmDto.updatedAt()
        );
    }
}
