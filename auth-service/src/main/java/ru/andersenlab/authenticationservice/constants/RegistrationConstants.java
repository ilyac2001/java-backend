package ru.andersenlab.authenticationservice.constants;

import com.ibm.icu.text.Transliterator;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RegistrationConstants {
    public static final Transliterator TRANSLITERATOR = Transliterator.getInstance("Russian-Latin/BGN");
    public static final String EMAIL_REGEX = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}";
    public static final int PASSWORD_LENGTH = 12;
    public static final int DEFAULT_EMPLOYEE_STATUS_ID = 1;
    public static final String EMAIL_DOMAIN = "@xbank.com";
    public static final String NEW_EMPLOYEE_MESSAGE = "Новый сотрудник успешно добавлен.";
}
