package ru.andersenlab.apigateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.andersenlab.apigateway.domain.exception.UnauthorizedException;
import ru.andersenlab.apigateway.util.JwtTokenUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthorizationFilterTest {

    @InjectMocks
    private JwtAuthorizationFilter filter;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private GatewayFilterChain chain;

    @Test
    void shouldThrowUnauthorizedExceptionWhenAuthorizationHeaderIsMissing() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain))
                .expectErrorMatches(throwable -> throwable instanceof UnauthorizedException &&
                        throwable.getMessage().equals("Missing or invalid Authorization header"))
                .verify();
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenAuthorizationHeaderHasInvalidFormat() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/")
                .header(HttpHeaders.AUTHORIZATION, "InvalidHeader")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain))
                .expectErrorMatches(throwable -> throwable instanceof UnauthorizedException &&
                        throwable.getMessage().equals("Missing or invalid Authorization header"))
                .verify();
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenTokenIsExpired() {
        String expiredToken = "expired.jwt.token";
        MockServerHttpRequest request = MockServerHttpRequest.get("/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtTokenUtil.getClaimsReactive(expiredToken))
                .thenReturn(Mono.error(new ExpiredJwtException(null, null, "Token expired")));

        StepVerifier.create(filter.filter(exchange, chain))
                .expectErrorMatches(throwable -> throwable instanceof ExpiredJwtException &&
                        throwable.getMessage().equals("Token expired"))
                .verify();
    }

    @Test
    void shouldThrowForbiddenExceptionWhenSignatureVerificationFails() {
        String invalidToken = "invalid.signature.token";
        MockServerHttpRequest request = MockServerHttpRequest.get("/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtTokenUtil.getClaimsReactive(invalidToken))
                .thenReturn(Mono.error(new SignatureException("Invalid signature")));

        StepVerifier.create(filter.filter(exchange, chain))
                .expectErrorMatches(throwable -> throwable instanceof SignatureException &&
                        throwable.getMessage().equals("Invalid signature"))
                .verify();
    }

    @Test
    void shouldAddEmployeeIdAndAccessLevelIdHeadersOnSuccessfulAuthorization() {
        String validToken = "valid.jwt.token";
        MockServerHttpRequest request = MockServerHttpRequest.get("/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Claims claims = mock(Claims.class);
        when(jwtTokenUtil.getClaimsReactive(validToken)).thenReturn(Mono.just(claims));
        when(claims.getSubject()).thenReturn("12345");
        when(claims.get("access_level_id", Integer.class)).thenReturn(2);

        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .expectComplete()
                .verify();

        assertThat(exchange.getRequest().getHeaders().getFirst("Authorized-Employee")).isEqualTo("12345");
        assertThat(exchange.getRequest().getHeaders().getFirst("Authorized-EmployeeAccessLevel")).isEqualTo("2");
    }
}
