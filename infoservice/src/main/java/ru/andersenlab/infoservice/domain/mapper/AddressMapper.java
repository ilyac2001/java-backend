package ru.andersenlab.infoservice.domain.mapper;

import org.mapstruct.*;
import ru.andersenlab.infoservice.domain.dto.AddressDto;
import ru.andersenlab.infoservice.domain.dto.AddressOutputDTO;
import ru.andersenlab.infoservice.domain.model.Address;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AddressMapper {
    @Mapping(source = "countryName", target = "country.countryName")
    @Mapping(source = "stateName", target = "state.stateName")
    @Mapping(source = "cityType", target = "city.cityType")
    @Mapping(source = "cityName", target = "city.cityName")
    @Mapping(source = "streetType", target = "street.streetType")
    @Mapping(source = "streetName", target = "street.streetName")
    Address toEntity(AddressDto addressDto);

    @Mapping(source = "country.countryName", target = "countryName")
    @Mapping(source = "state.stateName", target = "stateName")
    @Mapping(source = "city.cityType", target = "cityType")
    @Mapping(source = "city.cityName", target = "cityName")
    @Mapping(source = "street.streetType", target = "streetType")
    @Mapping(source = "street.streetName", target = "streetName")
    AddressDto toDto(Address address);

    @Mapping(source = "countryName", target = "country.countryName")
    @Mapping(source = "stateName", target = "state.stateName")
    @Mapping(source = "cityType", target = "city.cityType")
    @Mapping(source = "cityName", target = "city.cityName")
    @Mapping(source = "streetType", target = "street.streetType")
    @Mapping(source = "streetName", target = "street.streetName")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAddressFromDto(AddressDto addressDto, @MappingTarget Address address);
    @Mappings({
            @Mapping(source = "country.countryName", target = "countryName"),
            @Mapping(source = "state.stateName", target = "stateName"),
            @Mapping(source = "city.cityName", target = "cityName"),
            @Mapping(source = "city.cityType", target = "cityType"),
            @Mapping(source = "street.streetName", target = "streetName"),
            @Mapping(source = "street.streetType", target = "streetType"),
    })
    AddressOutputDTO toAddressOutputDTO(Address address);
}
