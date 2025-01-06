package ru.andersenlab.authenticationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.andersenlab.authenticationservice.domain.dto.CreateEmployeeDto;
import ru.andersenlab.authenticationservice.domain.dto.EmployeeDto;
import ru.andersenlab.authenticationservice.domain.dto.EmployeeInfoProjection;
import ru.andersenlab.authenticationservice.domain.dto.LoginRequestDto;
import ru.andersenlab.authenticationservice.domain.dto.LoginResponseDto;
import ru.andersenlab.authenticationservice.service.EmployeeService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    CreateEmployeeDto createEmployeeDto;
    EmployeeDto employeeDto;
    LoginRequestDto loginRequest;
    LoginResponseDto loginResponse;

    @BeforeEach
    void setUp() {
        UUID employeeId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        createEmployeeDto = CreateEmployeeDto.builder()
                .firstName("John")
                .lastName("Doe")
                .employeeId("123e4567-e89b-12d3-a456-426614174000")
                .accessLevelId(1)
                .build();

        employeeDto = EmployeeDto.builder()
                .employeeId(employeeId)
                .login("john.doe@xbank.com")
                .password("Password123")
                .message("Новый сотрудник успешно добавлен.")
                .build();

        loginRequest = LoginRequestDto.builder()
                .workEmail("john.doe@xbank.com")
                .password("Password123")
                .build();

        loginResponse = LoginResponseDto.builder()
                .employeeId(employeeId)
                .accessLevelId(1)
                .build();
    }

    @Test
    void registerNewEmployee_ShouldReturnCreated_WhenValidDataProvided() throws Exception {
        when(employeeService.registerNewEmployee(any(CreateEmployeeDto.class))).thenReturn(employeeDto);

        mockMvc.perform(post("/api/v1/auth-service/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEmployeeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value("john.doe@xbank.com"))
                .andExpect(jsonPath("$.password").value("Password123"))
                .andExpect(jsonPath("$.message").value("Новый сотрудник успешно добавлен."));
    }

    @Test
    void registerNewEmployee_ShouldReturnBadRequest_WhenInvalidDataProvided() throws Exception {
        CreateEmployeeDto invalidCreateEmployee = createEmployeeDto.toBuilder().firstName(null).build();

        mockMvc.perform(post("/api/v1/auth-service/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCreateEmployee)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturnOk_WhenValidDataProvided() throws Exception {
        when(employeeService.loginEmployeeByEmailAndPassword(any(LoginRequestDto.class))).thenReturn(loginResponse);

        mockMvc.perform(get("/api/v1/auth-service/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").isNotEmpty())
                .andExpect(jsonPath("$.accessLevelId").value(1));
    }

    @Test
    void login_ShouldReturnBadRequest_WhenInvalidDataProvided() throws Exception {
        LoginRequestDto invalidLoginRequest = loginRequest.toBuilder().workEmail(null).build();

        mockMvc.perform(get("/api/v1/auth-service/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEmployeeInfo_ShouldReturnEmployeeInfo_WhenEmployeeAuthorized() throws Exception {
        UUID employeeId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        EmployeeInfoProjection employeeInfo = new EmployeeInfoProjection() {
            @Override
            public UUID getEmployeeId() {
                return employeeId;
            }

            @Override
            public String getWorkEmail() {
                return "john.doe@xbank.com";
            }

            @Override
            public Integer getAccessLevelId() {
                return 1;
            }

            @Override
            public Integer getEmployeeStatusId() {
                return 1;
            }
        };

        when(employeeService.readActiveEmployeeInfo(employeeId)).thenReturn(employeeInfo);

        mockMvc.perform(get("/api/v1/auth-service/employees/{employee_id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(employeeId.toString()))
                .andExpect(jsonPath("$.workEmail").value("john.doe@xbank.com"))
                .andExpect(jsonPath("$.accessLevelId").value(1))
                .andExpect(jsonPath("$.employeeStatusId").value(1));
    }
}