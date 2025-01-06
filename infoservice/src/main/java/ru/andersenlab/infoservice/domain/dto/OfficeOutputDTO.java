package ru.andersenlab.infoservice.domain.dto;

import java.util.List;
import java.util.UUID;

public record OfficeOutputDTO(
        UUID officeId,
        String officeName,
        String phoneNumber,
        String officeEmail,
        AddressOutputDTO address,
        List<WeekDayOutputDTO> workTime,
        boolean isDeleted) {
}
