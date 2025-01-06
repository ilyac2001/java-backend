package ru.andersenlab.infoservice.domain.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AddressOutputDTO(
        UUID addressId,
        String countryName,
        String stateName,
        String cityName,
        String cityType,
        String streetName,
        String streetType,
        String houseNumber,
        String houseBody,
        String zipCode,
        BigDecimal lon,
        BigDecimal lat
) {
}
