package ru.andersenlab.authenticationservice.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConfigurationConstants {
    public static final String PASSWORD_UPDATE_ERROR = "Произошла ошибка при обновлении пароля";
    public static final String AUTHENTICATION_ERROR = "Введены неверные данные сотрудника";
    public static final String FORBIDDEN_ERROR = "Отказ в доступе сотруднику";
    public static final String INVALID_INPUT_ERROR = "Ошибка валидации: введены некорректные данные";
    public static final String ENTITY_NOT_FOUND_ERROR = "Ошибка данные не найдены";
}
