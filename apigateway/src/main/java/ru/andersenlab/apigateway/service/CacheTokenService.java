package ru.andersenlab.apigateway.service;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CacheTokenService {

    Mono<Void> cacheToken(UUID employeeId, String token, String tokenType);

    Mono<String> getToken(UUID employeeId, String tokenType);

    Mono<Void> evictToken(UUID employeeId, String tokenType);
}
