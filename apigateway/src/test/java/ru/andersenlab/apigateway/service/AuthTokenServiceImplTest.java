package ru.andersenlab.apigateway.service;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.andersenlab.apigateway.domain.dto.AccountCredentialsDTO;
import ru.andersenlab.apigateway.domain.dto.LoginRequestDTO;
import ru.andersenlab.apigateway.domain.exception.AuthServiceUnavailableException;
import ru.andersenlab.apigateway.domain.exception.ForbiddenException;
import ru.andersenlab.apigateway.domain.exception.InternalServerErrorException;
import ru.andersenlab.apigateway.domain.exception.UnauthorizedException;
import ru.andersenlab.apigateway.service.impl.AuthTokenServiceImpl;
import ru.andersenlab.apigateway.util.JwtTokenUtil;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WireMockTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthTokenServiceImplTest {

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(8080))
            .build();

    private WebClient webClient;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private CacheTokenService cacheTokenService;

    private AuthTokenServiceImpl authTokenService;

    @BeforeEach
    void setUp() {
        webClient = WebClient.create("http://localhost:8080");
        authTokenService = new AuthTokenServiceImpl(jwtTokenUtil, webClient, cacheTokenService);

        when(jwtTokenUtil.generateRefreshToken(any(AccountCredentialsDTO.class)))
                .thenReturn(Mono.just("mock-refresh-token"));
        when(jwtTokenUtil.generateAccessToken(any(AccountCredentialsDTO.class)))
                .thenReturn(Mono.just("mock-access-token"));
        when(cacheTokenService.cacheToken(any(UUID.class), anyString(), eq("refresh")))
                .thenReturn(Mono.empty());
    }

    @Test
    void shouldCreateTokenAndCacheRefreshToken() {
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/login"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"employeeId\": \"123e4567-e89b-12d3-a456-426614174000\", \"accessLevelId\": 2}")));

        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "password");

        StepVerifier.create(authTokenService.createToken(loginRequest))
                .assertNext(tokenDTO -> {
                    assertThat(tokenDTO).isNotNull();
                    assertThat(tokenDTO.refreshToken()).isEqualTo("mock-refresh-token");
                    assertThat(tokenDTO.accessToken()).isEqualTo("mock-access-token");

                    verify(cacheTokenService).cacheToken(
                            UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                            "mock-refresh-token",
                            "refresh"
                    );
                })
                .verifyComplete();
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenCredentialsAreInvalid() {
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/login"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Invalid credentials\"}")));

        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "invalid-password");

        StepVerifier.create(authTokenService.createToken(loginRequest))
                .expectErrorMatches(throwable -> throwable instanceof UnauthorizedException &&
                        throwable.getMessage().equals("Вы ввели неверный E-mail и/или пароль. Попробуйте ещё раз."))
                .verify();
    }

    @Test
    void shouldThrowForbiddenExceptionWhenUserIsBlocked() {
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/login"))
                .willReturn(aResponse()
                        .withStatus(403)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"User is blocked\"}")));

        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "password");

        StepVerifier.create(authTokenService.createToken(loginRequest))
                .expectErrorMatches(throwable -> throwable instanceof ForbiddenException &&
                        throwable.getMessage().equals("Пользователь заблокирован. Пожалуйста, обратитесь к администратору."))
                .verify();
    }

    @Test
    void shouldThrowServiceUnavailableExceptionWhenServiceIsDown() {
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/login"))
                .willReturn(aResponse()
                        .withStatus(503)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Service is down\"}")));

        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "password");

        StepVerifier.create(authTokenService.createToken(loginRequest))
                .expectErrorMatches(throwable -> throwable instanceof AuthServiceUnavailableException &&
                        throwable.getMessage().equals("Сервис недоступен для обработки запроса. Попробуйте позже."))
                .verify();
    }

    @Test
    void shouldThrowInternalServerErrorWhenServerErrorOccurs() {
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/login"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Internal server error\"}")));

        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "password");

        StepVerifier.create(authTokenService.createToken(loginRequest))
                .expectErrorMatches(throwable -> throwable instanceof InternalServerErrorException &&
                        throwable.getMessage().equals("Сервер не может обработать запрос из-за ошибок в данных."))
                .verify();
    }
}
