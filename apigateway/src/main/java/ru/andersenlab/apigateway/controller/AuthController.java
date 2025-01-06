package ru.andersenlab.apigateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.andersenlab.apigateway.domain.dto.JwtRefreshAndAccessTokenDTO;
import ru.andersenlab.apigateway.domain.dto.LoginRequestDTO;
import ru.andersenlab.apigateway.service.AuthTokenService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/api-gateway")
public class AuthController {

    private final AuthTokenService authTokenService;

    @PostMapping("/login")
    public Mono<ResponseEntity<JwtRefreshAndAccessTokenDTO>> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return authTokenService.createToken(loginRequestDTO)
                .map(ResponseEntity::ok);
    }
}
