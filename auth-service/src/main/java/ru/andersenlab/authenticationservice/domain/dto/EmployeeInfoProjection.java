package ru.andersenlab.authenticationservice.domain.dto;

import java.util.UUID;

public interface EmployeeInfoProjection {
    UUID getEmployeeId();
    String getWorkEmail();
    Integer getAccessLevelId();
    Integer getEmployeeStatusId();
}
