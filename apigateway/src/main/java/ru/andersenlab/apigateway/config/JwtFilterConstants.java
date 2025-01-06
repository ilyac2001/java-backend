package ru.andersenlab.apigateway.config;

import org.springframework.http.HttpHeaders;

public final class JwtFilterConstants {

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTH_HEADER = HttpHeaders.AUTHORIZATION;
    public static final String AUTHORIZED_EMPLOYEE_HEADER = "Authorized-Employee";
    public static final String AUTHORIZED_EMPLOYEE_ACCESS_LEVEL_HEADER = "Authorized-EmployeeAccessLevel";

    public static final String CLAIM_ACCESS_LEVEL_ID = "access_level_id";

    public static final String ERROR_MISSING_OR_INVALID_HEADER = "Missing or invalid Authorization header";

    private JwtFilterConstants() {
    }
}
