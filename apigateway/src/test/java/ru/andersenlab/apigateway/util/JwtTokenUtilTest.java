package ru.andersenlab.apigateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import ru.andersenlab.apigateway.domain.dto.AccountCredentialsDTO;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtTokenUtilTest {

    private JwtTokenUtil jwtTokenUtil;

    private final String secret = "this-is-a-test-secret-that-is-long-enough-to-pass-validationadasdasdas";
    private final long accessTokenExpiration = 60000;
    private final long refreshTokenExpiration = 3600000;

    @BeforeEach
    void setup() throws Exception {
        jwtTokenUtil = new JwtTokenUtil();

        setPrivateField(jwtTokenUtil, "secret", secret);
        setPrivateField(jwtTokenUtil, "accessTokenExpiration", accessTokenExpiration);
        setPrivateField(jwtTokenUtil, "refreshTokenExpiration", refreshTokenExpiration);
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void shouldGenerateValidAccessToken() {
        AccountCredentialsDTO account = new AccountCredentialsDTO(UUID.randomUUID(), 2);

        StepVerifier.create(jwtTokenUtil.generateAccessToken(account))
                .assertNext(token -> {
                    assertThat(token).isNotEmpty();
                    Claims claims = jwtTokenUtil.getClaimsReactive(token).block();
                    assertThat(claims).isNotNull();
                    assertThat(claims.getSubject()).isEqualTo(account.employeeId().toString());
                    assertThat(claims.get("access_level_id", Integer.class)).isEqualTo(account.accessLevelId());
                })
                .verifyComplete();
    }

    @Test
    void shouldGenerateValidRefreshToken() {
        AccountCredentialsDTO account = new AccountCredentialsDTO(UUID.randomUUID(), 2);

        StepVerifier.create(jwtTokenUtil.generateRefreshToken(account))
                .assertNext(token -> {
                    assertThat(token).isNotEmpty();
                    Claims claims = jwtTokenUtil.getClaimsReactive(token).block();
                    assertThat(claims).isNotNull();
                    assertThat(claims.getSubject()).isEqualTo(account.employeeId().toString());
                })
                .verifyComplete();
    }

    @Test
    void shouldDetectExpiredToken() throws Exception {
        AccountCredentialsDTO account = new AccountCredentialsDTO(UUID.randomUUID(), 2);
        setPrivateField(jwtTokenUtil, "accessTokenExpiration", -1000L);

        StepVerifier.create(jwtTokenUtil.generateAccessToken(account)
                        .flatMap(jwtTokenUtil::isExpired))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldNotDetectExpiredTokenForValidToken() {
        AccountCredentialsDTO account = new AccountCredentialsDTO(UUID.randomUUID(), 2);

        StepVerifier.create(jwtTokenUtil.generateAccessToken(account)
                        .flatMap(jwtTokenUtil::isExpired))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionForExpiredTokenInGetClaims() throws Exception {
        AccountCredentialsDTO account = new AccountCredentialsDTO(UUID.randomUUID(), 2);
        setPrivateField(jwtTokenUtil, "accessTokenExpiration", -1000L);

        StepVerifier.create(jwtTokenUtil.generateAccessToken(account)
                        .flatMap(jwtTokenUtil::getClaimsReactive))
                .expectErrorMatches(throwable -> throwable instanceof ExpiredJwtException &&
                        throwable.getMessage().contains("JWT expired"))
                .verify();
    }

    @Test
    void shouldGenerateValidPasswordRecoveryToken() {
        AccountCredentialsDTO account = new AccountCredentialsDTO(UUID.randomUUID(), 2);

        StepVerifier.create(jwtTokenUtil.generatePasswordRecoveryToken(account))
                .assertNext(token -> {
                    assertThat(token).isNotEmpty();
                    Claims claims = jwtTokenUtil.getClaimsReactive(token).block();
                    assertThat(claims).isNotNull();
                    assertThat(claims.getSubject()).isEqualTo(account.employeeId().toString());
                })
                .verifyComplete();
    }
}
