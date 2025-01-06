package ru.andersenlab.infoservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andersenlab.infoservice.domain.model.City;
import ru.andersenlab.infoservice.repository.CityRepository;
import ru.andersenlab.infoservice.service.CityService;

@RequiredArgsConstructor
@Service
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;

    @Override
    public void addCity(City entity) {
        cityRepository.save(entity);
    }
}
