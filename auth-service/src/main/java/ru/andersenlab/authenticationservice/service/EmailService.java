package ru.andersenlab.authenticationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.andersenlab.authenticationservice.repository.AccountCredentialsRepository;

import static ru.andersenlab.authenticationservice.constants.RegistrationConstants.EMAIL_DOMAIN;
import static ru.andersenlab.authenticationservice.constants.RegistrationConstants.TRANSLITERATOR;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailService {

    private final AccountCredentialsRepository accountCredentialsRepository;

    private static final String EMAIL_TEMPLATE = "%s.%s%s%s";
    private static final String ADDITIONAL_NUMBER_START = StringUtils.EMPTY;
    private static final int ADDITIONAL_NUMBER_INITIAL = 1;

    public String generateUniqueEmail(String firstName, String lastName) {
        String email = createTransliteratedEmail(firstName, lastName, ADDITIONAL_NUMBER_START);
        int additionalNumber = ADDITIONAL_NUMBER_INITIAL;

        while (accountCredentialsRepository.existsByWorkEmail(email)) {
            email = createTransliteratedEmail(firstName, lastName, String.valueOf(additionalNumber++));
        }

        log.debug("Для сотрудника сгенерирован уникальный email: {}", email);
        return email;
    }

    private String createTransliteratedEmail(String firstName, String lastName, String additionalNumber) {
        String transFirstName = TRANSLITERATOR.transliterate(firstName).toLowerCase();
        String transLastName = TRANSLITERATOR.transliterate(lastName).toLowerCase();

        return EMAIL_TEMPLATE.formatted(transFirstName, transLastName, additionalNumber, EMAIL_DOMAIN);
    }
}