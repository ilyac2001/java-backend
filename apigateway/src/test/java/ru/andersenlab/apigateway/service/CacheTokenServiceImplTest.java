package ru.andersenlab.apigateway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;
import ru.andersenlab.apigateway.service.impl.CacheTokenServiceImpl;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CacheTokenServiceImplTest {

    @Autowired
    private CacheTokenServiceImpl cacheTokenService;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Container
    private static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.0.11")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @BeforeEach
    void clearCache() {
        RedisConnection connection = redisConnectionFactory.getConnection();
        connection.flushDb();
    }

    @Test
    void shouldCacheAndRetrieveTokenWithTTL() {
        UUID employeeId = UUID.randomUUID();
        String refreshToken = "refresh-token";
        String recoveryToken = "recovery-token";

        StepVerifier.create(cacheTokenService.cacheToken(employeeId, refreshToken, "refresh"))
                .expectComplete()
                .verify();

        StepVerifier.create(cacheTokenService.getToken(employeeId, "refresh"))
                .expectNext(refreshToken)
                .expectComplete()
                .verify();

        String refreshKey = String.format("%s:%s", employeeId, "refresh");
        RedisConnection connection = redisConnectionFactory.getConnection();
        Long refreshTTL = connection.keyCommands().ttl(refreshKey.getBytes());
        assertThat(refreshTTL).isGreaterThan(0);

        StepVerifier.create(cacheTokenService.cacheToken(employeeId, recoveryToken, "recovery"))
                .expectComplete()
                .verify();

        StepVerifier.create(cacheTokenService.getToken(employeeId, "recovery"))
                .expectNext(recoveryToken)
                .expectComplete()
                .verify();

        String recoveryKey = String.format("%s:%s", employeeId, "recovery");
        Long recoveryTTL = connection.keyCommands().ttl(recoveryKey.getBytes());
        assertThat(recoveryTTL).isGreaterThan(0);
    }

    @Test
    void shouldEvictTokenFromCache() {
        UUID employeeId = UUID.randomUUID();
        String refreshToken = "refresh-token";
        String recoveryToken = "recovery-token";

        StepVerifier.create(cacheTokenService.cacheToken(employeeId, refreshToken, "refresh"))
                .expectComplete()
                .verify();

        StepVerifier.create(cacheTokenService.evictToken(employeeId, "refresh"))
                .expectComplete()
                .verify();

        StepVerifier.create(cacheTokenService.getToken(employeeId, "refresh"))
                .expectNextCount(0)
                .expectComplete()
                .verify();

        StepVerifier.create(cacheTokenService.cacheToken(employeeId, recoveryToken, "recovery"))
                .expectComplete()
                .verify();

        StepVerifier.create(cacheTokenService.evictToken(employeeId, "recovery"))
                .expectComplete()
                .verify();

        StepVerifier.create(cacheTokenService.getToken(employeeId, "recovery"))
                .expectNextCount(0)
                .expectComplete()
                .verify();
    }
}
