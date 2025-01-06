package ru.andersenlab.apigateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.andersenlab.apigateway.domain.dto.JwtRefreshAndAccessTokenDTO;
import ru.andersenlab.apigateway.domain.dto.LoginRequestDTO;
import ru.andersenlab.apigateway.domain.exception.AuthServiceUnavailableException;
import ru.andersenlab.apigateway.domain.exception.ForbiddenException;
import ru.andersenlab.apigateway.domain.exception.InternalServerErrorException;
import ru.andersenlab.apigateway.domain.exception.UnauthorizedException;
import ru.andersenlab.apigateway.service.AuthTokenService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AuthTokenService authTokenService;

    @Test
    void shouldLoginAndReturnTokens() {
        String expectedRefreshToken = "mock-refresh-token";
        String expectedAccessToken = "mock-access-token";

        JwtRefreshAndAccessTokenDTO mockResponse = new JwtRefreshAndAccessTokenDTO(expectedAccessToken, expectedRefreshToken);

        when(authTokenService.createToken(any(LoginRequestDTO.class)))
                .thenReturn(Mono.just(mockResponse));

        String loginRequestJson = "{ \"email\": \"test@example.com\", \"password\": \"password\", \"workEmail\": \"testwork@example.com\" }";

        webTestClient.post().uri("/api/v1/api-gateway/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequestJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accessToken").isEqualTo(expectedAccessToken)
                .jsonPath("$.refreshToken").isEqualTo(expectedRefreshToken);
    }

    @Test
    void shouldReturnBadRequestWhenInvalidCredentials() {
        String loginRequestJson = "{ \"email\": \"test@example.com\", \"password\": \"invalid-password\", \"workEmail\": \"testwork@example.com\" }";

        when(authTokenService.createToken(any(LoginRequestDTO.class)))
                .thenReturn(Mono.error(new UnauthorizedException("Invalid credentials")));

        webTestClient.post().uri("/api/v1/api-gateway/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequestJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.title").isEqualTo("401 UNAUTHORIZED")
                .jsonPath("$.detail").isEqualTo("Invalid credentials");
    }

    @Test
    void shouldReturnForbiddenErrorWhenAccessDenied() {
        String loginRequestJson = "{ \"email\": \"test@example.com\", \"password\": \"password\", \"workEmail\": \"testwork@example.com\" }";

        when(authTokenService.createToken(any(LoginRequestDTO.class)))
                .thenReturn(Mono.error(new ForbiddenException("Access forbidden")));

        webTestClient.post().uri("/api/v1/api-gateway/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequestJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.title").isEqualTo("403 FORBIDDEN")
                .jsonPath("$.detail").isEqualTo("Access forbidden");
    }

    @Test
    void shouldReturnServiceUnavailableWhenAuthServiceIsDown() {
        String loginRequestJson = "{ \"email\": \"test@example.com\", \"password\": \"password\", \"workEmail\": \"testwork@example.com\" }";

        when(authTokenService.createToken(any(LoginRequestDTO.class)))
                .thenReturn(Mono.error(new AuthServiceUnavailableException("Service is down")));

        webTestClient.post().uri("/api/v1/api-gateway/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequestJson)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.title").isEqualTo("503 SERVICE UNAVAILABLE")
                .jsonPath("$.detail").isEqualTo("Service is down");
    }

    @Test
    void shouldReturnInternalServerErrorWhenServerErrorOccurs() {
        String loginRequestJson = "{ \"email\": \"test@example.com\", \"password\": \"password\", \"workEmail\": \"testwork@example.com\" }";

        when(authTokenService.createToken(any(LoginRequestDTO.class)))
                .thenReturn(Mono.error(new InternalServerErrorException("Internal server error")));

        webTestClient.post().uri("/api/v1/api-gateway/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequestJson)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.title").isEqualTo("500 INTERNAL SERVER ERROR")
                .jsonPath("$.detail").isEqualTo("Internal server error");
    }

    @Test
    void shouldReturnInternalServerErrorForUnknownException() {
        String loginRequestJson = "{ \"email\": \"test@example.com\", \"password\": \"password\", \"workEmail\": \"testwork@example.com\" }";

        when(authTokenService.createToken(any(LoginRequestDTO.class)))
                .thenReturn(Mono.error(new RuntimeException("Unexpected error")));

        webTestClient.post().uri("/api/v1/api-gateway/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequestJson)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.title").isEqualTo("500 INTERNAL SERVER ERROR")
                .jsonPath("$.detail").isEqualTo("Произошла непредвиденная ошибка. Попробуйте позже.");
    }
}
