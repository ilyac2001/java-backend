package ru.andersenlab.infoservice.domain.dto;

import jakarta.validation.constraints.*;
import ru.andersenlab.infoservice.validation.GroupCreateAtm;

import java.math.BigDecimal;

public record AddressDto(

    @NotNull(message = "Название страны не должно быть пустым",
            groups = GroupCreateAtm.class)
    @Size(
            min = 1, max = 30,
            message = "Название страны должно быть не короче 1 символа и не длиннее 30 символов")
    @Pattern(
            regexp = "^(?![\\s-])[A-Za-zА-Яа-я\\s-]{1,30}$",
            message = "Неверный формат для названия страны. " +
                    "Можно использовать только буквы кириллицы и латиницы, " +
                    "не может начинаться с пробела или дефиса")
    String countryName,

    @NotNull(message = "Название региона не должно быть пустым",
            groups = GroupCreateAtm.class)
    @Size(
            min = 1, max = 30,
            message = "Название региона должно быть не короче 1 символа и не длиннее 30 символов")
    @Pattern(
            regexp = "^(?![\\s-])[A-Za-zА-Яа-я\\s-]{1,30}$",
            message = "Неверный формат для названия региона. " +
                    "Можно использовать только буквы кириллицы и латиницы, " +
                    "не может начинаться с пробела или дефиса")
    String stateName,

    @NotNull(message = "Тип города не должно быть пустым",
            groups = GroupCreateAtm.class)
    @Size(
            min = 1, max = 30,
            message = "Тип города должно быть не короче 1 символа и не длиннее 30 символов")
    @Pattern(
            regexp = "^(?![\\s-])[A-Za-zА-Яа-я\\s-]{1,30}$",
            message = "Неверный формат для типа города. " +
                    "Можно использовать только буквы кириллицы и латиницы, " +
                    "не может начинаться с пробела или дефиса")
    String cityType,

    @NotNull(message = "Название города не должно быть пустым",
            groups = GroupCreateAtm.class)
    @Size(
            min = 1, max = 30,
            message = "Название города должно быть не короче 1 символа и не длиннее 30 символов")
    @Pattern(
            regexp = "^(?![\\s-])[A-Za-zА-Яа-я\\s-]{1,30}$",
            message = "Неверный формат для названия города. " +
                    "Можно использовать только буквы кириллицы и латиницы, " +
                    "не может начинаться с пробела или дефиса")
    String cityName,

    @NotNull(message = "Тип улицы не должно быть пустым",
            groups = GroupCreateAtm.class)
    @Size(
            min = 1, max = 30,
            message = "Тип улицы должно быть не короче 1 символа и не длиннее 30 символов")
    @Pattern(
            regexp = "^(?![\\s-])[A-Za-zА-Яа-я\\s-]{1,30}$",
            message = "Неверный формат для типа улицы. " +
                    "Можно использовать только буквы кириллицы и латиницы, " +
                    "не может начинаться с пробела или дефиса")
    String streetType,

    @NotNull(message = "Название улицы не должно быть пустым",
            groups = GroupCreateAtm.class)
    @Size(
            min = 1, max = 30,
            message = "Название улицы должно быть не короче 1 символа и не длиннее 30 символов")
    @Pattern(
            regexp = "^(?![\\s-])[A-Za-zА-Яа-я\\s-]{1,30}$",
            message = "Неверный формат для названия улицы. " +
                    "Можно использовать только буквы кириллицы и латиницы, " +
                    "не может начинаться с пробела или дефиса")
    String streetName,

    @NotNull(message = "Номер строения не должен быть пустым",
            groups = GroupCreateAtm.class)
    @Size(
            min = 1, max = 30,
            message = "Номер строения должен быть не короче 1 символа и не длиннее 30 символов")
    @Pattern(
            regexp = "^(?!0)(?![A-Za-zА-Яа-я\\s/-])[A-Za-zА-Яа-я0-9/\\-]{1,30}$",
            message = "Неверный формат для номера строения. " +
                    "Можно использовать только кириллицу, латиницу и цифры, " +
                    "не может начинаться с цифры или буквы")
    String houseBody,

    @NotNull(message = "Номер дома не должен быть пустым",
            groups = GroupCreateAtm.class)
    @Size(
            min = 1, max = 30,
            message = "Номер дома должен быть не короче 1 символа и не длиннее 30 символов")
    @Pattern(
            regexp = "^(?!0)(?![A-Za-zА-Яа-я\\s/-])[A-Za-zА-Яа-я0-9/\\-]{1,30}$",
            message = "Неверный формат для номера дома. " +
                    "Можно использовать только кириллицу, латиницу и цифры, " +
                    "не может начинаться с цифры или буквы")
    String houseNumber,

    @NotNull(message = "Значение координаты не должно быть пустым",
            groups = GroupCreateAtm.class)
    @DecimalMin(value = "-180.00000000",
            message = "Долгота должна быть не менее -180.00000000")
    @DecimalMax(value = "180.00000000",
            message = "Долгота должна быть не более 180.00000000")
    BigDecimal lon,

    @NotNull(message = "Значение координаты не должно быть пустым",
            groups = GroupCreateAtm.class)
    @DecimalMin(value = "-90.00000000",
            message = "Широта должна быть не менее -90.00000000")
    @DecimalMax(value = "90.00000000",
            message = "Широта должна быть не более 90.00000000")
    BigDecimal lat,

    @NotNull(message = "Почтовый индекс не должен быть пустым",
            groups = GroupCreateAtm.class)
    @Size(min = 1, max = 6,
            message = "Почтовый индекс должен содержать от 1 до 6 символов")
    String zipCode
){}