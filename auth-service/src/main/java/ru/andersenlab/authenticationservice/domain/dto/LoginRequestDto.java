package ru.andersenlab.authenticationservice.domain.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import static ru.andersenlab.authenticationservice.constants.RegistrationConstants.EMAIL_REGEX;

@Builder(toBuilder = true)
public record LoginRequestDto(

        @NotNull(message = "Поле workEmail не может быть пустым.")
        @Email(regexp = EMAIL_REGEX, message = "Некорректный формат почтового адреса.")
        String workEmail,

        @NotBlank(message = "Поле password не может быть пустым или содержать только пробелы.")
        String password) {
}
