package ru.andersenlab.authenticationservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.andersenlab.authenticationservice.repository.AccountCredentialsRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.andersenlab.authenticationservice.constants.RegistrationConstants.EMAIL_DOMAIN;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private AccountCredentialsRepository accountCredentialsRepository;

    @InjectMocks
    private EmailService emailService;

    private String firstName;
    private String lastName;
    private String expectedEmail;
    private String uniqueEmail;

    @BeforeEach
    void setUp() {
        firstName = "John";
        lastName = "Doe";
        expectedEmail = "john.doe" + EMAIL_DOMAIN;
        uniqueEmail = "john.doe1" + EMAIL_DOMAIN;
    }

    @Test
    void generateUniqueEmail_ShouldReturnUniqueEmail_WhenEmailNotExists() {
        when(accountCredentialsRepository.existsByWorkEmail(expectedEmail)).thenReturn(false);

        String result = emailService.generateUniqueEmail(firstName, lastName);

        assertEquals(expectedEmail, result);
        verify(accountCredentialsRepository).existsByWorkEmail(expectedEmail);
    }

    @Test
    void generateUniqueEmail_ShouldGenerateEmailWithAdditionalNumber_WhenConflictExists() {
        when(accountCredentialsRepository.existsByWorkEmail(expectedEmail)).thenReturn(true);
        when(accountCredentialsRepository.existsByWorkEmail(uniqueEmail)).thenReturn(false);

        String result = emailService.generateUniqueEmail(firstName, lastName);

        assertEquals(uniqueEmail, result);
        verify(accountCredentialsRepository).existsByWorkEmail(expectedEmail);
        verify(accountCredentialsRepository).existsByWorkEmail(uniqueEmail);
    }
}