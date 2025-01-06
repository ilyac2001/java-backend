package ru.andersenlab.infoservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andersenlab.infoservice.domain.model.Address;
import ru.andersenlab.infoservice.repository.AddressRepository;
import ru.andersenlab.infoservice.service.*;

@RequiredArgsConstructor
@Service
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final CountryService countryService;
    private final StateService stateService;
    private final CityService cityService;
    private final StreetService streetService;
    
    @Override
    public void addAddress(Address address) {
        countryService.addCountry(address.getCountry());
        stateService.addState(address.getState());
        cityService.addCity(address.getCity());
        streetService.addStreet(address.getStreet());
        addressRepository.save(address);
    }
}
