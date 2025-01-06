package ru.andersenlab.authenticationservice.domain.enumerated;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmployeeStatusEnum {
    ACTIVE(1, "Активный"),
    BLOCKED(2, "Заблокирован"),
    DISMISSED(3, "Уволен");

    private final int id;
    private final String name;

    public static EmployeeStatusEnum fromId(int id) {
        for (EmployeeStatusEnum status : values()) {
            if (status.id == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Неизвестный статус с ID: " + id);
    }
}
