package ru.andersenlab.infoservice.service;

import ru.andersenlab.infoservice.domain.dto.AtmDto;

import java.util.UUID;

public interface AtmService {
    void addAtm(AtmDto atmDto);
    AtmDto updateAtm(UUID atmIdm, AtmDto atmDto);
}