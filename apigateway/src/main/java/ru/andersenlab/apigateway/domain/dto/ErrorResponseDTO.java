package ru.andersenlab.apigateway.domain.dto;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        String title,
        String detail,
        String request,
        LocalDateTime timestamp
) {
}
