package ru.andersenlab.authenticationservice.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.andersenlab.authenticationservice.domain.dto.CreateEmployeeDto;
import ru.andersenlab.authenticationservice.domain.dto.EmployeeDto;
import ru.andersenlab.authenticationservice.domain.mapper.EmployeeMapper;
import ru.andersenlab.authenticationservice.domain.mapper.EmployeeMapperHelper;
import ru.andersenlab.authenticationservice.model.AccessLevel;
import ru.andersenlab.authenticationservice.model.AccountCredentials;
import ru.andersenlab.authenticationservice.model.EmployeeStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.andersenlab.authenticationservice.constants.RegistrationConstants.NEW_EMPLOYEE_MESSAGE;
import static ru.andersenlab.authenticationservice.constants.RegistrationConstants.PASSWORD_LENGTH;

class EmployeeMapperTest {

    @Mock
    private EmployeeMapperHelper employeeMapperHelper;

    @InjectMocks
    private EmployeeMapper employeeMapper = Mappers.getMapper(EmployeeMapper.class);

    private String password;
    private String encodedPassword;
    private String email;
    private CreateEmployeeDto createEmployeeDto;
    private EmployeeDto employeeDto;
    private AccessLevel accessLevel;
    private EmployeeStatus employeeStatus;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        password = "Password123!";
        encodedPassword = "$2a$10$encodedpassword";
        email = "john.doe@xbank.com";

        createEmployeeDto = CreateEmployeeDto.builder()
                .firstName("John")
                .lastName("Doe")
                .employeeId("123e4567-e89b-12d3-a456-426614174000")
                .accessLevelId(1)
                .build();

        employeeDto = EmployeeDto.builder()
                .employeeId(UUID.randomUUID())
                .login("john.doe@xbank.com")
                .password(password)
                .build();

        accessLevel = AccessLevel.builder()
                .accessLevelId(1)
                .levelName("Сотрудник банка")
                .build();

        employeeStatus = EmployeeStatus.builder()
                .employeeStatusId(1)
                .statusName("Активен")
                .build();
    }

    @Test
    void toEmployeeDtoWithPassword_ShouldMapCorrectly_WhenValidDataProvided() {
        when(employeeMapperHelper.generateUniqueEmail(createEmployeeDto)).thenReturn(email);
        when(employeeMapperHelper.generatePassword(PASSWORD_LENGTH)).thenReturn(password);

        EmployeeDto result = employeeMapper.toEmployeeDtoWithPassword(createEmployeeDto, PASSWORD_LENGTH, NEW_EMPLOYEE_MESSAGE);

        assertEquals(email, result.login());
        assertEquals(password, result.password());
        assertEquals(NEW_EMPLOYEE_MESSAGE, result.message());

        verify(employeeMapperHelper).generateUniqueEmail(createEmployeeDto);
        verify(employeeMapperHelper).generatePassword(PASSWORD_LENGTH);
    }

    @Test
    void toNewAccountCredentials_ShouldMapCorrectly_WhenValidDataProvided() {
        when(employeeMapperHelper.encodePassword(employeeDto)).thenReturn(encodedPassword);

        AccountCredentials result = employeeMapper.toNewAccountCredentials(employeeDto, accessLevel, employeeStatus);

        assertEquals(employeeDto.employeeId(), result.getEmployeeId());
        assertEquals(employeeDto.login(), result.getWorkEmail());
        assertEquals(encodedPassword, result.getPassword());
        assertEquals(accessLevel, result.getAccessLevel());
        assertEquals(employeeStatus, result.getEmployeeStatus());
        assertEquals(Boolean.TRUE, result.isTemporaryPassword());

        verify(employeeMapperHelper).encodePassword(employeeDto);
    }
}