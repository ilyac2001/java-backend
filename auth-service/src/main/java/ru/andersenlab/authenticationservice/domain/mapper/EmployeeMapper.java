package ru.andersenlab.authenticationservice.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;
import ru.andersenlab.authenticationservice.domain.dto.CreateEmployeeDto;
import ru.andersenlab.authenticationservice.domain.dto.EmployeeDto;
import ru.andersenlab.authenticationservice.model.AccessLevel;
import ru.andersenlab.authenticationservice.model.AccountCredentials;
import ru.andersenlab.authenticationservice.model.EmployeeStatus;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = EmployeeMapperHelper.class)
public interface EmployeeMapper {

    @Mappings({
            @Mapping(target = "login", qualifiedByName = "generateUniqueEmail", source = "createEmployeeDto"),
            @Mapping(target = "password", qualifiedByName = "generatePassword", source = "passwordLength"),
            @Mapping(target = "message", source = "message")
    })
    EmployeeDto toEmployeeDtoWithPassword(CreateEmployeeDto createEmployeeDto, int passwordLength, String message);

    @Mappings(value = {
            @Mapping(target = "employeeId", source = "employeeDto.employeeId"),
            @Mapping(target = "workEmail", source = "employeeDto.login"),
            @Mapping(target = "password", qualifiedByName = "encodePassword", source = "employeeDto"),
            @Mapping(target = "isTemporaryPassword", constant = "true")
    })
    AccountCredentials toNewAccountCredentials(EmployeeDto employeeDto, AccessLevel accessLevel, EmployeeStatus employeeStatus);
}
