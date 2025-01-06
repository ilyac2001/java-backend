package ru.andersenlab.authenticationservice.domain.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.andersenlab.authenticationservice.domain.exception.AuthenticationException;
import ru.andersenlab.authenticationservice.domain.exception.EntityNotFoundException;
import ru.andersenlab.authenticationservice.domain.exception.ForbiddenException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.andersenlab.authenticationservice.constants.ConfigurationConstants.AUTHENTICATION_ERROR;
import static ru.andersenlab.authenticationservice.constants.ConfigurationConstants.ENTITY_NOT_FOUND_ERROR;
import static ru.andersenlab.authenticationservice.constants.ConfigurationConstants.FORBIDDEN_ERROR;
import static ru.andersenlab.authenticationservice.constants.ConfigurationConstants.INVALID_INPUT_ERROR;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEntityNotFoundException(HttpServletRequest request, EntityNotFoundException e) {
        log.error("EntityNotFoundException encountered: {}", e.getMessage());
        return ErrorResponse.builder()
                .title(ENTITY_NOT_FOUND_ERROR)
                .detail(e.getMessage())
                .request(request.getMethod() + " " + request.getRequestURI())
                .time(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleEntityNotFoundException(HttpServletRequest request, AuthenticationException e) {
        log.error("AuthenticationException encountered: {}", e.getMessage());
        return ErrorResponse.builder()
                .title(AUTHENTICATION_ERROR)
                .detail(e.getMessage())
                .request(request.getMethod() + " " + request.getRequestURI())
                .time(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenExceptionException(HttpServletRequest request, ForbiddenException e) {
        log.error("ForbiddenException encountered: {}", e.getMessage());
        return ErrorResponse.builder()
                .title(FORBIDDEN_ERROR)
                .detail(e.getMessage())
                .request(request.getMethod() + " " + request.getRequestURI())
                .time(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("MethodArgumentNotValidException encountered: {}", e.getBindingResult().getFieldErrors());
        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .title(INVALID_INPUT_ERROR)
                .detail(String.join(", ", errors))
                .request(request.getMethod() + " " + request.getRequestURI())
                .time(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(HttpServletRequest request, IllegalArgumentException e) {
        log.error("IllegalArgumentException encountered: {}", e.getMessage());
        return ErrorResponse.builder()
                .title("Invalid request")
                .detail(e.getMessage())
                .request(request.getMethod() + " " + request.getRequestURI())
                .time(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(HttpServletRequest request, Exception e) {
        log.error("Unhandled exception encountered: {}", e.getMessage());
        return ErrorResponse.builder()
                .title("Internal server error")
                .detail(e.getMessage())
                .request(request.getMethod() + " " + request.getRequestURI())
                .time(LocalDateTime.now())
                .build();
    }

    @Value
    @Builder
    public static class ErrorResponse {
        String title;
        String detail;
        String request;
        LocalDateTime time;
    }
}
