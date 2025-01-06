package ru.andersenlab.authenticationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.andersenlab.authenticationservice.domain.dto.CreateEmployeeDto;
import ru.andersenlab.authenticationservice.domain.dto.EmployeeDto;
import ru.andersenlab.authenticationservice.domain.dto.EmployeeInfoProjection;
import ru.andersenlab.authenticationservice.domain.dto.LoginRequestDto;
import ru.andersenlab.authenticationservice.domain.dto.LoginResponseDto;
import ru.andersenlab.authenticationservice.service.EmployeeService;

import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth-service")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/employees")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDto registerNewEmployee(@Valid @RequestBody CreateEmployeeDto createEmployeeDto) {
        log.info("Получен запрос на создание нового профиля для firstname: {}, lastname: {}",
                createEmployeeDto.firstName(), createEmployeeDto.lastName());
        EmployeeDto response = employeeService.registerNewEmployee(createEmployeeDto);
        log.info("Ответ на создание нового профиля для firstname: {}, lastname: {}", createEmployeeDto.firstName(),
                createEmployeeDto.lastName());
        return response;
    }

    @GetMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        log.info("Получен запрос на вход для email: {}", loginRequestDto.workEmail());
        LoginResponseDto response = employeeService.loginEmployeeByEmailAndPassword(loginRequestDto);
        log.info("Ответ на вход сгенерирован для email: {}", loginRequestDto.workEmail());
        return response;
    }

    @GetMapping("/employees/{employee_id}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeInfoProjection getEmployeeInfo(@PathVariable("employee_id") UUID employeeId) {
        return employeeService.readActiveEmployeeInfo(employeeId);
    }
}