package ru.andersenlab.authenticationservice.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
public record CreateEmployeeDto(

        @NotBlank(message = "Поле firstName не может быть пустым или содержать только пробелы.")
        String firstName,

        @NotBlank(message = "Поле lastName не может быть пустым или содержать только пробелы.")
        String lastName,

        @NotBlank(message = "Поле employeeId не может быть пустым или содержать только пробелы.")
        String employeeId,

        @NotNull(message = "Поле accessLevelId не может быть null.")
        Integer accessLevelId) {
}
