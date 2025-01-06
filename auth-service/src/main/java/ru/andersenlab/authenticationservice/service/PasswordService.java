package ru.andersenlab.authenticationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.andersenlab.authenticationservice.domain.exception.AuthenticationException;

import static org.passay.IllegalCharacterRule.ERROR_CODE;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordEncoder passwordEncoder;
    private static final int REQUIRED_NUM_OF_CHARS = 1;
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+";

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public void checkPassword(String requestPassword, String accountCredentialsPassword) {
        if (!passwordEncoder.matches(requestPassword, accountCredentialsPassword)) {
            log.warn("Ошибка авторизации. Неверный пароль.");
            throw new AuthenticationException("Неверный почтовый адрес и/или пароль.Попробуйте еще раз.");
        }
    }

    public String generatePassword(int passwordLength) {
        PasswordGenerator gen = new PasswordGenerator();

        CharacterRule lowerCaseRule = generateRule(EnglishCharacterData.LowerCase, REQUIRED_NUM_OF_CHARS);
        CharacterRule upperCaseRule = generateRule(EnglishCharacterData.UpperCase, REQUIRED_NUM_OF_CHARS);
        CharacterRule digitRule = generateRule(EnglishCharacterData.Digit, REQUIRED_NUM_OF_CHARS);

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return ERROR_CODE;
            }

            public String getCharacters() {
                return SPECIAL_CHARS;
            }
        };
        CharacterRule splCharRule = generateRule(specialChars, REQUIRED_NUM_OF_CHARS);

        return gen.generatePassword(passwordLength, splCharRule, lowerCaseRule,
                upperCaseRule, digitRule);
    }

    private CharacterRule generateRule(CharacterData characterData, int numOfChars) {
        CharacterRule rule = new CharacterRule(characterData);
        rule.setNumberOfCharacters(numOfChars);
        return rule;
    }
}
