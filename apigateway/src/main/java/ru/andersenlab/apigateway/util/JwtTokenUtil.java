package ru.andersenlab.apigateway.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.andersenlab.apigateway.domain.dto.AccountCredentialsDTO;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh.token.expiration}")
    private long refreshTokenExpiration;

    public Mono<String> generateAccessToken(AccountCredentialsDTO account) {
        return Mono.fromCallable(() -> JWT.create()
                .withSubject(account.employeeId().toString())
                .withClaim("access_level_id", account.accessLevelId())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .sign(Algorithm.HMAC512(secret)));
    }

    public Mono<String> generateRefreshToken(AccountCredentialsDTO account) {
        return Mono.fromCallable(() -> JWT.create()
                .withSubject(account.employeeId().toString())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .sign(Algorithm.HMAC512(secret)));
    }

    public Mono<Boolean> isExpired(String token) {
        return getClaimsReactive(token)
                .flatMap(claims -> Mono.just(false))
                .onErrorResume(ExpiredJwtException.class, e -> Mono.just(true));
    }

    public Mono<Claims> getClaimsReactive(String token) {
        return Mono.fromCallable(() -> {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        });
    }

    public Mono<String> generatePasswordRecoveryToken(AccountCredentialsDTO account) {
        return Mono.fromCallable(() -> JWT.create()
                .withSubject(account.employeeId().toString())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .sign(Algorithm.HMAC512(secret)));
    }
}
