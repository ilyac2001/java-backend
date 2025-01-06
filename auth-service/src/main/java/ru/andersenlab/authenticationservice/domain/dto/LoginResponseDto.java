package ru.andersenlab.authenticationservice.domain.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record LoginResponseDto(
        UUID employeeId,
        Integer accessLevelId) {
}
