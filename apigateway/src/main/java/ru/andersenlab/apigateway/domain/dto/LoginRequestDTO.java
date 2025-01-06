package ru.andersenlab.apigateway.domain.dto;

import jakarta.validation.constraints.NotEmpty;

public record LoginRequestDTO(

        @NotEmpty
        String workEmail,

        @NotEmpty
        String password
) {
}
