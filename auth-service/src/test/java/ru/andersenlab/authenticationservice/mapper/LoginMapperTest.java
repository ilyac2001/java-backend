package ru.andersenlab.authenticationservice.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.andersenlab.authenticationservice.domain.dto.LoginResponseDto;
import ru.andersenlab.authenticationservice.domain.mapper.LoginMapper;
import ru.andersenlab.authenticationservice.model.AccessLevel;
import ru.andersenlab.authenticationservice.model.AccountCredentials;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginMapperTest {

    private final LoginMapper loginMapper = Mappers.getMapper(LoginMapper.class);

    private UUID employeeId;
    private AccessLevel accessLevel;

    private AccountCredentials accountCredential;

    @BeforeEach
    void setUp() {
        employeeId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        accessLevel = AccessLevel.builder()
                .accessLevelId(1)
                .levelName("Сотрудник банка")
                .build();

        accountCredential = AccountCredentials.builder()
                .employeeId(employeeId)
                .accessLevel(accessLevel)
                .build();
    }

    @Test
    void toLoginResponseDto_ShouldMapCorrectly_WhenValidDataProvided() {
        LoginResponseDto result = loginMapper.toLoginResponseDto(accountCredential);

        assertEquals(employeeId, result.employeeId());
        assertEquals(accessLevel.getAccessLevelId(), result.accessLevelId());
    }
}