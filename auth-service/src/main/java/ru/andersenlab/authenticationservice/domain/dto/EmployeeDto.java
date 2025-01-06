package ru.andersenlab.authenticationservice.domain.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record EmployeeDto(
        UUID employeeId,
        String login,
        String password,
        String message) {
}
