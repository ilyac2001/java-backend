package ru.andersenlab.apigateway.domain.dto;

import jakarta.validation.constraints.NotNull;

public record JwtRefreshTokenDTO(

        @NotNull(message = "Id пользователя не доложен быть пустым")
        String employeeId,

        @NotNull(message = "Токен не должен быть пустым")
        String refreshToken
) {
}
