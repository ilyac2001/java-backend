package ru.andersenlab.apigateway.domain.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;
import ru.andersenlab.apigateway.domain.dto.ErrorResponseDTO;
import ru.andersenlab.apigateway.domain.exception.AuthServiceUnavailableException;
import ru.andersenlab.apigateway.domain.exception.ForbiddenException;
import ru.andersenlab.apigateway.domain.exception.InternalServerErrorException;
import ru.andersenlab.apigateway.domain.exception.UnauthorizedException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleUnauthorizedException(UnauthorizedException ex) {
        log.error("Unauthorized error: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildErrorResponse("401 UNAUTHORIZED", ex.getMessage(), HttpStatus.UNAUTHORIZED)));
    }

    @ExceptionHandler(ForbiddenException.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleForbiddenException(ForbiddenException ex) {
        log.error("Forbidden error: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildErrorResponse("403 FORBIDDEN", ex.getMessage(), HttpStatus.FORBIDDEN)));
    }

    @ExceptionHandler(AuthServiceUnavailableException.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleServiceUnavailableException(AuthServiceUnavailableException ex) {
        log.error("Auth service unavailable: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(buildErrorResponse("503 SERVICE UNAVAILABLE", ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE)));
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleInternalServerErrorException(InternalServerErrorException ex) {
        log.error("Internal server error: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse("500 INTERNAL SERVER ERROR", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR)));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse("500 INTERNAL SERVER ERROR", "Произошла непредвиденная ошибка. Попробуйте позже.", HttpStatus.INTERNAL_SERVER_ERROR)));
    }

    private ErrorResponseDTO buildErrorResponse(String title, String detail, HttpStatus status) {
        return new ErrorResponseDTO(
                title,
                detail,
                "Request could not be processed",
                LocalDateTime.now()
        );
    }
}
