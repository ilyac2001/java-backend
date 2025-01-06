package ru.andersenlab.apigateway.domain.dto;

import java.util.UUID;

public record AccountCredentialsDTO(
        UUID employeeId,
        Integer accessLevelId
) {
}
