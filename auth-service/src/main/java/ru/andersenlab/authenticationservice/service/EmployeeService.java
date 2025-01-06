package ru.andersenlab.authenticationservice.service;

import ru.andersenlab.authenticationservice.domain.dto.*;

import java.util.UUID;

public interface EmployeeService {

    EmployeeDto registerNewEmployee(CreateEmployeeDto createEmployeeDto);

    LoginResponseDto loginEmployeeByEmailAndPassword(LoginRequestDto request);

    EmployeeInfoProjection readActiveEmployeeInfo(UUID employeeId);
}
