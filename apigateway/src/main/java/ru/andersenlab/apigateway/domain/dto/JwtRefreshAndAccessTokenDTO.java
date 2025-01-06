package ru.andersenlab.apigateway.domain.dto;

public record JwtRefreshAndAccessTokenDTO(

        String accessToken,

        String refreshToken
) {
}
