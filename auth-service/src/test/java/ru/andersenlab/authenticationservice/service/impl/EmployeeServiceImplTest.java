package ru.andersenlab.authenticationservice.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import ru.andersenlab.authenticationservice.service.PasswordService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ru.andersenlab.authenticationservice.constants.RegistrationConstants.DEFAULT_EMPLOYEE_STATUS_ID;
import static ru.andersenlab.authenticationservice.constants.RegistrationConstants.NEW_EMPLOYEE_MESSAGE;
import static ru.andersenlab.authenticationservice.constants.RegistrationConstants.PASSWORD_LENGTH;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private AccountCredentialsRepository accountCredentialsRepository;
    @Mock
    private EmployeeStatusRepository employeeRepository;
    @Mock
    private AccessLevelRepository accessLevelRepository;
    @Mock
    private PasswordService passwordService;
    @Mock
    private EmployeeMapper employeeMapper;
    @Mock
    private LoginMapper loginMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private CreateEmployeeDto createEmployeeDto;
    private EmployeeDto employeeDto;
    private AccessLevel accessLevel;
    private EmployeeStatus employeeStatus;
    private LoginRequestDto loginRequest;
    private AccountCredentials accountCredentials;
    private EmployeeInfoProjection employeeInfoProjection;
    private LoginResponseDto expectedResponse;
    private Integer invalidId;

    @BeforeEach
    void setUp() {
        UUID employeeId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        createEmployeeDto = CreateEmployeeDto.builder()
                .firstName("John")
                .lastName("Doe")
                .employeeId("123e4567-e89b-12d3-a456-426614174000")
                .accessLevelId(1)
                .build();
        accessLevel = AccessLevel.builder()
                .accessLevelId(1)
                .levelName("Сотрудник банка")
                .build();
        employeeStatus = EmployeeStatus.builder()
                .employeeStatusId(1)
                .statusName("Активен")
                .build();
        employeeDto = EmployeeDto.builder()
                .employeeId(employeeId)
                .login("john.doe@xbank.com")
                .password("SecurePassword123")
                .message("Новый сотрудник успешно добавлен.")
                .build();
        loginRequest = LoginRequestDto.builder()
                .workEmail("john.doe@xbank.com")
                .password("SecurePassword123")
                .build();
        accountCredentials = AccountCredentials.builder()
                .employeeId(employeeId)
                .workEmail("john.doe@xbank.com")
                .password(new BCryptPasswordEncoder().encode("SecurePassword123"))
                .employeeStatus(employeeStatus)
                .accessLevel(new AccessLevel())
                .isTemporaryPassword(false)
                .build();
        employeeInfoProjection = new EmployeeInfoProjection() {
            @Override
            public UUID getEmployeeId() {
                return accountCredentials.getEmployeeId();
            }

            @Override
            public String getWorkEmail() {
                return accountCredentials.getWorkEmail();
            }

            @Override
            public Integer getAccessLevelId() {
                return accountCredentials.getAccessLevel().getAccessLevelId();
            }

            @Override
            public Integer getEmployeeStatusId() {
                return accountCredentials.getEmployeeStatus().getEmployeeStatusId();
            }
        };
        expectedResponse = LoginResponseDto.builder()
                .employeeId(employeeId)
                .accessLevelId(1)
                .build();
        invalidId = -1;
    }

    @Test
    void registerNewEmployee_ShouldReturnEmployeeDto_WhenSuccessful() {
        when(accessLevelRepository.findById(createEmployeeDto.accessLevelId())).thenReturn(Optional.of(accessLevel));
        when(employeeRepository.findById(DEFAULT_EMPLOYEE_STATUS_ID)).thenReturn(Optional.of(employeeStatus));
        when(employeeMapper.toEmployeeDtoWithPassword(createEmployeeDto, PASSWORD_LENGTH, NEW_EMPLOYEE_MESSAGE)).thenReturn(employeeDto);

        AccountCredentials accountCredentials = new AccountCredentials();
        when(employeeMapper.toNewAccountCredentials(employeeDto, accessLevel, employeeStatus)).thenReturn(accountCredentials);

        EmployeeDto result = employeeService.registerNewEmployee(createEmployeeDto);

        verify(accountCredentialsRepository).save(accountCredentials);
        assertEquals(employeeDto, result);
    }

    @Test
    void registerNewEmployee_ShouldThrowEntityNotFoundException_WhenAccessLevelNotFound() {
        createEmployeeDto = createEmployeeDto.toBuilder().accessLevelId(invalidId).build();

        when(accessLevelRepository.findById(createEmployeeDto.accessLevelId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> employeeService.registerNewEmployee(createEmployeeDto));
    }

    @Test
    void registerNewEmployee_ShouldThrowEntityNotFoundException_WhenEmployeeStatusNotFound() {
        when(accessLevelRepository.findById(createEmployeeDto.accessLevelId())).thenReturn(Optional.of(accessLevel));
        when(employeeRepository.findById(DEFAULT_EMPLOYEE_STATUS_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> employeeService.registerNewEmployee(createEmployeeDto));
    }

    @Test
    void loginEmployeeByEmailAndPassword_ShouldReturnLoginResponseDto_WhenSuccessful() {
        when(accountCredentialsRepository.findByWorkEmail(loginRequest.workEmail())).thenReturn(Optional.of(accountCredentials));
        when(loginMapper.toLoginResponseDto(accountCredentials)).thenReturn(new LoginResponseDto(UUID.randomUUID(), 1));

        LoginResponseDto result = employeeService.loginEmployeeByEmailAndPassword(loginRequest);

        assertNotNull(result);
        assertEquals(expectedResponse.accessLevelId(), result.accessLevelId());
    }

    @Test
    void loginEmployeeByEmailAndPassword_ShouldThrowAuthenticationException_WhenEmailNotFound() {
        when(accountCredentialsRepository.findByWorkEmail(loginRequest.workEmail())).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> employeeService.loginEmployeeByEmailAndPassword(loginRequest));
    }

    @Test
    void loginEmployeeByEmailAndPassword_ShouldThrowForbiddenException_WhenEmployeeBlocked() {
        EmployeeStatus blockedStatus = new EmployeeStatus(2, "Заблокирован");
        accountCredentials = accountCredentials.toBuilder().employeeStatus(blockedStatus).build();

        when(accountCredentialsRepository.findByWorkEmail(loginRequest.workEmail())).thenReturn(Optional.of(accountCredentials));

        assertThrows(ForbiddenException.class, () -> employeeService.loginEmployeeByEmailAndPassword(loginRequest));
    }

    @Test
    void loginEmployeeByEmailAndPassword_ShouldThrowForbiddenException_WhenEmployeeDismissed() {
        EmployeeStatus blockedStatus = new EmployeeStatus(3, "Уволен");
        accountCredentials = accountCredentials.toBuilder().employeeStatus(blockedStatus).build();

        when(accountCredentialsRepository.findByWorkEmail(loginRequest.workEmail())).thenReturn(Optional.of(accountCredentials));

        assertThrows(ForbiddenException.class, () -> employeeService.loginEmployeeByEmailAndPassword(loginRequest));
    }

    @Test
    void readActiveEmployeeInfo_ShouldReturnEmployeeInfoProjection_WhenEmployeeIsActive() {
        UUID employeeId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        when(accountCredentialsRepository.findByEmployeeIdAndEmployeeStatus_EmployeeStatusId(employeeId, EmployeeStatusEnum.ACTIVE.getId()))
                .thenReturn(Optional.of(employeeInfoProjection));

        EmployeeInfoProjection result = employeeService.readActiveEmployeeInfo(employeeId);

        assertNotNull(result);
        assertEquals(employeeId, result.getEmployeeId());
    }

    @Test
    void readActiveEmployeeInfo_ShouldThrowAuthenticationException_WhenEmployeeNotFound() {
        UUID invalidEmployeeId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        when(accountCredentialsRepository.findByEmployeeIdAndEmployeeStatus_EmployeeStatusId(invalidEmployeeId, EmployeeStatusEnum.ACTIVE.getId()))
                .thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> employeeService.readActiveEmployeeInfo(invalidEmployeeId));
    }

    @Test
    void readActiveEmployeeInfo_ShouldThrowAuthenticationException_WhenEmployeeStatusIsNotActive() {
        UUID employeeId = UUID.fromString("450e8400-e29b-41d4-a716-446655440001");
        EmployeeStatus nonActiveStatus = new EmployeeStatus(2, "Заблокирован");
        accountCredentials = accountCredentials.toBuilder().employeeStatus(nonActiveStatus).build();

        when(accountCredentialsRepository.findByEmployeeIdAndEmployeeStatus_EmployeeStatusId(employeeId, EmployeeStatusEnum.ACTIVE.getId()))
                .thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> employeeService.readActiveEmployeeInfo(employeeId));
    }
}