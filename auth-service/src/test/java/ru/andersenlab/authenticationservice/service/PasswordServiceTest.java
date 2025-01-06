package ru.andersenlab.authenticationservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.andersenlab.authenticationservice.domain.exception.AuthenticationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.andersenlab.authenticationservice.constants.RegistrationConstants.PASSWORD_LENGTH;

@ExtendWith(MockitoExtension.class)
class PasswordServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordService passwordService;

    private String password;
    private String encodedPassword;

    @BeforeEach
    void setUp() {
        password = "Password123!";
        encodedPassword = "$2a$10$encodedpassword";
    }

    @Test
    void encodePassword_ShouldReturnEncodedPassword_WhenPasswordValid() {
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        String result = passwordService.encodePassword(password);

        assertEquals(encodedPassword, result);
        verify(passwordEncoder).encode(password);
    }

    @Test
    void checkPassword_ShouldNotThrowException_WhenPasswordsMatch() {
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        assertDoesNotThrow(() -> passwordService.checkPassword(password, encodedPassword));
        verify(passwordEncoder).matches(password, encodedPassword);
    }

    @Test
    void checkPassword_ShouldThrowAuthenticationException_WhenPasswordsDoNotMatch() {
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> passwordService.checkPassword(password, encodedPassword));

        assertEquals("Неверный почтовый адрес и/или пароль.Попробуйте еще раз.", exception.getMessage());
        verify(passwordEncoder).matches(password, encodedPassword);
    }

    @Test
    void generatePassword_ShouldReturnPassword_WhenLengthIsAndRuleValid() {
        String result = passwordService.generatePassword(PASSWORD_LENGTH);

        assertNotNull(result);
        assertTrue(result.matches(".*[a-z].*"), "Пароль должен содержать хотя бы один строчный символ.");
        assertTrue(result.matches(".*[A-Z].*"), "Password should contain at least one uppercase character.");
        assertTrue(result.matches(".*[0-9].*"), "Пароль должен содержать хотя бы одну цифру.");
        assertTrue(result.matches(".*[!@#$%^&*()_+].*"), "Пароль должен содержать хотя бы один специальный символ.");
        assertEquals(PASSWORD_LENGTH, result.length());
    }

    @Test
    void generatePassword_ShouldReturnPassword_WhenMinLengthIsAndRuleValid() {
        int minPasswordLength = 4;

        String result = passwordService.generatePassword(minPasswordLength);

        assertNotNull(result);
        assertTrue(result.matches(".*[a-z].*"), "Пароль должен содержать хотя бы один строчный символ.");
        assertTrue(result.matches(".*[A-Z].*"), "Password should contain at least one uppercase character.");
        assertTrue(result.matches(".*[0-9].*"), "Пароль должен содержать хотя бы одну цифру.");
        assertTrue(result.matches(".*[!@#$%^&*()_+].*"), "Пароль должен содержать хотя бы один специальный символ.");
        assertEquals(minPasswordLength, result.length());
    }

    @Test
    void generatePassword_ShouldThrowException_WhenLengthIsTooShort() {
        int invalidPasswordLength = 0;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> passwordService.generatePassword(invalidPasswordLength));

        assertEquals("length must be greater than 0", exception.getMessage());
    }
}