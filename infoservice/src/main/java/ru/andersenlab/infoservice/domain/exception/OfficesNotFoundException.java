package ru.andersenlab.infoservice.domain.exception;

public class OfficesNotFoundException extends RuntimeException {
    public OfficesNotFoundException(String message) {
        super(message);
    }
}
