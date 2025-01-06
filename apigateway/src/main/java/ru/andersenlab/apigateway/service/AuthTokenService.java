package ru.andersenlab.apigateway.service;

import reactor.core.publisher.Mono;
import ru.andersenlab.apigateway.domain.dto.JwtRefreshAndAccessTokenDTO;
import ru.andersenlab.apigateway.domain.dto.LoginRequestDTO;

public interface AuthTokenService {

    Mono<JwtRefreshAndAccessTokenDTO> createToken(LoginRequestDTO request);
}
