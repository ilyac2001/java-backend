package ru.andersenlab.authenticationservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andersenlab.authenticationservice.domain.dto.CreateEmployeeDto;
import ru.andersenlab.authenticationservice.domain.dto.EmployeeDto;
import ru.andersenlab.authenticationservice.domain.dto.EmployeeInfoProjection;
import ru.andersenlab.authenticationservice.domain.dto.LoginRequestDto;
import ru.andersenlab.authenticationservice.domain.dto.LoginResponseDto;
import ru.andersenlab.authenticationservice.domain.enumerated.EmployeeStatusEnum;
import ru.andersenlab.authenticationservice.domain.exception.AuthenticationException;
import ru.andersenlab.authenticationservice.domain.exception.EntityNotFoundException;
import ru.andersenlab.authenticationservice.domain.exception.ForbiddenException;
import ru.andersenlab.authenticationservice.domain.mapper.EmployeeMapper;
import ru.andersenlab.authenticationservice.domain.mapper.LoginMapper;
import ru.andersenlab.authenticationservice.model.AccessLevel;
import ru.andersenlab.authenticationservice.model.AccountCredentials;
import ru.andersenlab.authenticationservice.model.EmployeeStatus;
import ru.andersenlab.authenticationservice.repository.AccessLevelRepository;
import ru.andersenlab.authenticationservice.repository.AccountCredentialsRepository;
import ru.andersenlab.authenticationservice.repository.EmployeeStatusRepository;
import ru.andersenlab.authenticationservice.service.EmployeeService;
import ru.andersenlab.authenticationservice.service.PasswordService;

import java.util.UUID;

import static ru.andersenlab.authenticationservice.constants.RegistrationConstants.DEFAULT_EMPLOYEE_STATUS_ID;
import static ru.andersenlab.authenticationservice.constants.RegistrationConstants.NEW_EMPLOYEE_MESSAGE;
import static ru.andersenlab.authenticationservice.constants.RegistrationConstants.PASSWORD_LENGTH;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private static final String ENTITY_NOT_FOUND_TEMPLATE = "%s с id: %s не найден";

    private final AccountCredentialsRepository accountCredentialsRepository;
    private final EmployeeStatusRepository employeeRepository;
    private final AccessLevelRepository accessLevelRepository;
    private final PasswordService passwordService;
    private final EmployeeMapper employeeMapper;
    private final LoginMapper loginMapper;

    @Transactional
    @Override
    public EmployeeDto registerNewEmployee(CreateEmployeeDto createEmployeeDto) {

        AccessLevel accessLevel = getAccessLevelById(createEmployeeDto.accessLevelId());
        EmployeeStatus employeeStatus = getEmployeeStatusById(DEFAULT_EMPLOYEE_STATUS_ID);


        EmployeeDto response = employeeMapper.toEmployeeDtoWithPassword(
                createEmployeeDto,
                PASSWORD_LENGTH,
                NEW_EMPLOYEE_MESSAGE
        );

        AccountCredentials accountCredentials = employeeMapper.toNewAccountCredentials(
                response,
                accessLevel,
                employeeStatus
        );

        accountCredentialsRepository.save(accountCredentials);

        return response;
    }

    @Override
    public LoginResponseDto loginEmployeeByEmailAndPassword(LoginRequestDto request) {

        AccountCredentials accountCredentials = getAccountCredentials(request);
        passwordService.checkPassword(request.password(), accountCredentials.getPassword());
        checkEmployeeStatus(accountCredentials);

        return loginMapper.toLoginResponseDto(accountCredentials);
    }

    @Override
    public EmployeeInfoProjection readActiveEmployeeInfo(UUID employeeId){
        return accountCredentialsRepository
                .findByEmployeeIdAndEmployeeStatus_EmployeeStatusId(employeeId, EmployeeStatusEnum.ACTIVE.getId())
                .orElseThrow(() -> new AuthenticationException("Пользователь не авторизован"));
    }

    private AccountCredentials getAccountCredentials(LoginRequestDto request) {

        return accountCredentialsRepository
                .findByWorkEmail(request.workEmail())
                .orElseThrow(() -> new AuthenticationException("Неверный почтовый адрес и/или пароль.Попробуйте еще раз."));
    }

    private void checkEmployeeStatus(AccountCredentials accountCredentials) {

        EmployeeStatusEnum status = accountCredentials.getEmployeeStatus().toEnum();
        if (status == EmployeeStatusEnum.BLOCKED || status == EmployeeStatusEnum.DISMISSED) {
            throw new ForbiddenException("Пользователь %s. Пожалуйста, обратитесь к администратору.".formatted(status.getName()));
        }
    }

    private AccessLevel getAccessLevelById(Integer accessLevelId) {
        return accessLevelRepository.findById(accessLevelId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND_TEMPLATE.formatted("Уровень доступа сотрудника", accessLevelId)));
    }

    private EmployeeStatus getEmployeeStatusById(Integer employeeStatusId) {
        return employeeRepository.findById(employeeStatusId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND_TEMPLATE.formatted("Статус сотрудника", employeeStatusId)));
    }
}
