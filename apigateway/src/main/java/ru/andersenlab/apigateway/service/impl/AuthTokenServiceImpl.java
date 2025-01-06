package ru.andersenlab.apigateway.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.andersenlab.apigateway.domain.dto.AccountCredentialsDTO;
import ru.andersenlab.apigateway.domain.dto.JwtRefreshAndAccessTokenDTO;
import ru.andersenlab.apigateway.domain.dto.LoginRequestDTO;
import ru.andersenlab.apigateway.domain.exception.AuthServiceUnavailableException;
import ru.andersenlab.apigateway.domain.exception.ForbiddenException;
import ru.andersenlab.apigateway.domain.exception.InternalServerErrorException;
import ru.andersenlab.apigateway.domain.exception.UnauthorizedException;
import ru.andersenlab.apigateway.service.AuthTokenService;
import ru.andersenlab.apigateway.service.CacheTokenService;
import ru.andersenlab.apigateway.util.JwtTokenUtil;

@Service
@RequiredArgsConstructor
public class AuthTokenServiceImpl implements AuthTokenService {

    private final JwtTokenUtil jwtTokenUtil;
    private final WebClient webClient;
    private final CacheTokenService cacheTokenService;

    @Override
    public Mono<JwtRefreshAndAccessTokenDTO> createToken(LoginRequestDTO request) {
        return webClient.post()
                .uri("/api/v1/login")
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> Mono.error(mapException(response.statusCode()))
                )
                .bodyToMono(AccountCredentialsDTO.class)
                .flatMap(accountCredentialsDTO -> createJwtRefreshAndAccessTokenDto(accountCredentialsDTO)
                        .doOnNext(tokenDTO -> cacheTokenService.cacheToken(
                                accountCredentialsDTO.employeeId(),
                                tokenDTO.refreshToken(),
                                "refresh"
                        ).subscribe())
                );
    }

    private Mono<JwtRefreshAndAccessTokenDTO> createJwtRefreshAndAccessTokenDto(AccountCredentialsDTO accountCredentials) {
        return jwtTokenUtil.generateRefreshToken(accountCredentials)
                .flatMap(refreshToken -> jwtTokenUtil.generateAccessToken(accountCredentials)
                        .map(accessToken -> new JwtRefreshAndAccessTokenDTO(accessToken, refreshToken)));
    }

    private RuntimeException mapException(HttpStatusCode statusCode) {
        return switch (statusCode.value()) {
            case 401 -> new UnauthorizedException("Вы ввели неверный E-mail и/или пароль. Попробуйте ещё раз.");
            case 403 -> new ForbiddenException("Пользователь заблокирован. Пожалуйста, обратитесь к администратору.");
            case 503 -> new AuthServiceUnavailableException("Сервис недоступен для обработки запроса. Попробуйте позже.");
            case 500 -> new InternalServerErrorException("Сервер не может обработать запрос из-за ошибок в данных.");
            default -> new RuntimeException("Непредвиденная ошибка. Код ответа: " + statusCode.value());
        };
    }
}
