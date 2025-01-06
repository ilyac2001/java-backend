package ru.andersenlab.infoservice.controller.impl;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.andersenlab.infoservice.domain.dto.AtmDto;
import ru.andersenlab.infoservice.domain.dto.OutputAtmDto;
import ru.andersenlab.infoservice.domain.dto.ScheduleDto;
import ru.andersenlab.infoservice.service.AtmService;
import ru.andersenlab.infoservice.service.ScheduleService;
import ru.andersenlab.infoservice.validation.GroupCreateAtm;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/infoservice/atms")
@RequiredArgsConstructor
public class AtmControllerImpl{

    private final AtmService atmService;
    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<String> addAtm(@Validated({Default.class, GroupCreateAtm.class}) @RequestBody AtmDto atmDto) {
        atmService.addAtm(atmDto);
        return ResponseEntity.ok("Банкомат успешно добавлен");
    }

    @PatchMapping("{atmId}")
    public ResponseEntity<OutputAtmDto> updateAtm(@PathVariable UUID atmId,
                                                  @Validated(Default.class) @RequestBody AtmDto atmDto) {
        AtmDto responseAtmDetails = atmService.updateAtm(atmId, atmDto);
        ScheduleDto responseScheduleDetails = scheduleService.readSchedule(UUID.fromString(responseAtmDetails.scheduleId()));
        OutputAtmDto response = new OutputAtmDto(responseAtmDetails, responseScheduleDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
