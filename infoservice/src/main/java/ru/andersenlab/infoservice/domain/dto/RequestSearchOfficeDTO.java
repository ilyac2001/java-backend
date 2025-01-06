package ru.andersenlab.infoservice.domain.dto;

import jakarta.validation.constraints.Size;

public record RequestSearchOfficeDTO(
        @Size(max = 100, message = "Название офиса не должно превышать 100 символов")
        String officeName,
        @Size(max = 100, message = "Название города не должно превышать 100 символов")
        String cityName,
        @Size(max = 100, message = "Название улицы не должно превышать 100 символов")
        String streetName,
        @Size(max = 5, message = "Номер дома не должен превышать 5 символов")
        String houseNumber
) {
}
