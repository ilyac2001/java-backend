package ru.andersenlab.infoservice.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import ru.andersenlab.infoservice.validation.GroupCreateAtm;

import java.time.LocalDateTime;

public record AtmDto (
    @NotNull(message = "Номер банкомата не должен быть пустым.",
            groups = GroupCreateAtm.class)
    String atmName,

    @NotNull(message = "Описание местоположения не должно быть пустым",
            groups = GroupCreateAtm.class)
    @Pattern(regexp = "^[А-Яа-яЁё\"-./№]+(\\s[А-Яа-яЁё\"-./№]+)*$",
            message = "Для описания местоположения можно использовать только кириллицу и следующие символы: \",\", \"-\", \".\", \"/\", \"№\"")
    String locationDescription,

    @Valid
    AddressDto addressDetails,

    @NotNull(message = "Поле schedule_id не должно быть пустым.",
            groups = GroupCreateAtm.class)
    String scheduleId,

    boolean isDeleted,

    @NotNull(message = "Дата и время создания не должны быть пустыми",
            groups = GroupCreateAtm.class)
    LocalDateTime createdAt,

    @NotNull(message = "Дата и время обновления не должны быть пустыми")
    LocalDateTime updatedAt
) {}
