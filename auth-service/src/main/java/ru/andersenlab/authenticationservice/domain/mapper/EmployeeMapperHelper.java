package ru.andersenlab.authenticationservice.domain.mapper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import ru.andersenlab.authenticationservice.domain.dto.CreateEmployeeDto;
import ru.andersenlab.authenticationservice.domain.dto.EmployeeDto;
import ru.andersenlab.authenticationservice.service.EmailService;
import ru.andersenlab.authenticationservice.service.PasswordService;

@Component
@RequiredArgsConstructor
public class EmployeeMapperHelper {
    private final EmailService emailService;
    private final PasswordService passwordService;

    @Named("generateUniqueEmail")
    public String generateUniqueEmail(CreateEmployeeDto createEmployeeDto) {
        return emailService.generateUniqueEmail(createEmployeeDto.firstName(), createEmployeeDto.lastName());
    }

    @Named("generatePassword")
    public String generatePassword(int passwordLength) {
        return passwordService.generatePassword(passwordLength);
    }

    @Named("encodePassword")
    public String encodePassword(EmployeeDto employeeDto) {
        return passwordService.encodePassword(employeeDto.password());
    }
}