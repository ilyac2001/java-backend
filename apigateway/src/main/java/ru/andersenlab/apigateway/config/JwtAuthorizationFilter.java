package ru.andersenlab.apigateway.config;

import io.jsonwebtoken.Claims;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.andersenlab.apigateway.domain.exception.UnauthorizedException;
import ru.andersenlab.apigateway.util.JwtTokenUtil;

@Component
@RequiredArgsConstructor
@Order(-1)
@Slf4j
public class JwtAuthorizationFilter implements GatewayFilter {

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return extractToken(exchange)
                .flatMap(jwtTokenUtil::getClaimsReactive)
                .flatMap(claims -> applyClaimsToHeaders(exchange, claims, chain));
    }

    private Mono<String> extractToken(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(JwtFilterConstants.AUTH_HEADER))
                .filter(authHeader -> authHeader.startsWith(JwtFilterConstants.BEARER_PREFIX))
                .map(authHeader -> authHeader.substring(JwtFilterConstants.BEARER_PREFIX.length()))
                .switchIfEmpty(Mono.error(new UnauthorizedException(JwtFilterConstants.ERROR_MISSING_OR_INVALID_HEADER)));
    }

    private Mono<Void> applyClaimsToHeaders(ServerWebExchange exchange, Claims claims, GatewayFilterChain chain) {
        String employeeId = claims.getSubject();
        Integer accessLevelId = claims.get(JwtFilterConstants.CLAIM_ACCESS_LEVEL_ID, Integer.class);

        log.debug("Employee ID: {}", employeeId);
        log.debug("Access Level ID: {}", accessLevelId);

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(JwtFilterConstants.AUTHORIZED_EMPLOYEE_HEADER, employeeId)
                .header(JwtFilterConstants.AUTHORIZED_EMPLOYEE_ACCESS_LEVEL_HEADER,
                        accessLevelId != null ? accessLevelId.toString() : StringUtil.EMPTY_STRING)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }
}
