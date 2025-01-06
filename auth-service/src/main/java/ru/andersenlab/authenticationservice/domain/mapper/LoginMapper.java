package ru.andersenlab.authenticationservice.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.andersenlab.authenticationservice.domain.dto.LoginResponseDto;
import ru.andersenlab.authenticationservice.model.AccountCredentials;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface LoginMapper {

    @Mapping(target = "accessLevelId", source = "accessLevel.accessLevelId")
    LoginResponseDto toLoginResponseDto(AccountCredentials accountCredentials);
}
