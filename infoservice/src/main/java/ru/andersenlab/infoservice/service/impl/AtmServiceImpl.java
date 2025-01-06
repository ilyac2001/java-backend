package ru.andersenlab.infoservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andersenlab.infoservice.domain.dto.AtmDto;
import ru.andersenlab.infoservice.domain.exception.EntityNotFoundException;
import ru.andersenlab.infoservice.domain.mapper.AtmMapper;
import ru.andersenlab.infoservice.domain.model.Atm;
import ru.andersenlab.infoservice.repository.AtmRepository;
import ru.andersenlab.infoservice.service.*;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AtmServiceImpl implements AtmService {
    private final AtmMapper atmMapper;
    private final AtmRepository atmRepository;
    private final ScheduleService scheduleService;
    private final AddressService addressService;

    @Transactional
    @Override
    public void addAtm(AtmDto atmDto) {
        Atm atm = atmMapper.toEntity(atmDto);
        if(!scheduleService.existsById(atm.getSchedule().getScheduleId())){
            throw new EntityNotFoundException("Рассписания с ID: " + atm.getSchedule().getScheduleId() + "не найдено");
        }

        addressService.addAddress(atm.getAddress());
        atmRepository.save(atm);
    }

    @Transactional
    @Override
    public AtmDto updateAtm(UUID atmId, AtmDto atmDto) {
        Atm existingAtm = atmRepository.findById(atmId)
                .orElseThrow(() ->  new EntityNotFoundException("Банкомата с ID: " + atmId + "не найдено"));
        if(atmDto.scheduleId() != null) {
            existingAtm.setSchedule(scheduleService.getSchedule(UUID.fromString(atmDto.scheduleId())));
        }
        atmMapper.updateAtmFromDto(atmDto, existingAtm);
        return atmMapper.toDto(existingAtm);
    }
}
