package ru.andersenlab.apigateway.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.andersenlab.apigateway.service.CacheTokenService;

import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CacheTokenServiceImpl implements CacheTokenService {

    private final ReactiveStringRedisTemplate redisTemplate;

    private static final String TOKEN_KEY_PATTERN = "%s:%s";

    private String generateKey(UUID employeeId, String tokenType) {
        return String.format(TOKEN_KEY_PATTERN, employeeId, tokenType);
    }

    @Override
    public Mono<Void> cacheToken(UUID employeeId, String token, String tokenType) {
        String key = generateKey(employeeId, tokenType);
        log.info("Сохранение {} в кэш для employeeId: {}", tokenType, employeeId);
        long expiration = tokenType.equals("refresh") ? 43200 : 600;
        return redisTemplate.opsForValue()
                .set(key, token, Duration.ofSeconds(expiration))
                .then();
    }

    @Override
    public Mono<String> getToken(UUID employeeId, String tokenType) {
        String key = generateKey(employeeId, tokenType);
        log.info("Получение {} из кэша для employeeId: {}", tokenType, employeeId);
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Mono<Void> evictToken(UUID employeeId, String tokenType) {
        String key = generateKey(employeeId, tokenType);
        log.info("Удаление {} из кэша для employeeId: {}", tokenType, employeeId);
        return redisTemplate.delete(key).then();
    }
}
